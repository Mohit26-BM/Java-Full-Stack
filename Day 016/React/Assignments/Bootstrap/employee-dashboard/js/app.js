(function () {
  'use strict';

  const STORAGE_KEY = 'peoplehub-employees';
  const THEME_KEY = 'peoplehub-theme';
  const PAGE_SIZE = 6;

  const STATUS_BADGES = { Active: 'text-bg-success', 'On Leave': 'text-bg-warning', Inactive: 'text-bg-secondary' };

  const state = {
    employees: loadEmployees(),
    search: '',
    department: '',
    status: '',
    sortBy: 'id',
    sortDir: 'asc',
    page: 1,
    pendingDeleteId: null,
  };

  const $ = (id) => document.getElementById(id);
  const employeeModal = new bootstrap.Modal($('employeeModal'));
  const detailsModal = new bootstrap.Modal($('detailsModal'));
  const deleteModal = new bootstrap.Modal($('deleteModal'));
  const form = $('employeeForm');

  function loadEmployees() {
    try {
      const saved = JSON.parse(localStorage.getItem(STORAGE_KEY));
      if (Array.isArray(saved) && saved.every((e) => e && e.id && e.name)) return saved;
    } catch (error) {

    }
    return SAMPLE_EMPLOYEES.slice();
  }

  function saveEmployees() {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state.employees));
    } catch (error) {

    }
  }

  const escapeHtml = (text) => String(text).replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));

  const formatDate = (iso) => new Date(iso + 'T00:00:00').toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });

  const today = () => new Date().toISOString().slice(0, 10);

  function nextEmployeeId() {
    const highest = state.employees.reduce((max, e) => Math.max(max, Number(e.id.replace('EMP', '')) || 0), 100);
    return 'EMP' + (highest + 1);
  }

  function showAlert(message, type) {
    const alert = document.createElement('div');
    alert.className = `alert alert-${type} alert-dismissible fade show`;
    alert.setAttribute('role', 'alert');
    alert.innerHTML = `${escapeHtml(message)}<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>`;
    $('alertArea').replaceChildren(alert);
    setTimeout(() => bootstrap.Alert.getOrCreateInstance(alert).close(), 4000);
  }

  function visibleEmployees() {
    const term = state.search.trim().toLowerCase();
    const list = state.employees.filter((e) =>
      (!term || e.name.toLowerCase().includes(term) || e.id.toLowerCase().includes(term)) &&
      (!state.department || e.department === state.department) &&
      (!state.status || e.status === state.status));

    const direction = state.sortDir === 'asc' ? 1 : -1;
    return list.sort((a, b) => String(a[state.sortBy]).localeCompare(String(b[state.sortBy])) * direction);
  }

  function renderKpis() {
    const count = (status) => state.employees.filter((e) => e.status === status).length;
    const kpis = [
      { label: 'Total Employees', value: state.employees.length, icon: 'bi-people', color: 'primary' },
      { label: 'Active Employees', value: count('Active'), icon: 'bi-person-check', color: 'success' },
      { label: 'On Leave', value: count('On Leave'), icon: 'bi-calendar2-x', color: 'warning' },
      { label: 'Open Positions', value: OPEN_POSITIONS, icon: 'bi-briefcase', color: 'info' },
    ];
    $('kpiCards').innerHTML = kpis.map((kpi) => `
      <div class="col-12 col-sm-6 col-xl-3">
        <div class="card h-100 shadow-sm border-0 border-start border-4 border-${kpi.color}">
          <div class="card-body d-flex align-items-center gap-3">
            <div class="kpi-icon rounded-3 bg-${kpi.color}-subtle text-${kpi.color}-emphasis d-flex align-items-center justify-content-center fs-4 flex-shrink-0">
              <i class="bi ${kpi.icon}"></i>
            </div>
            <div>
              <div class="text-body-secondary small">${kpi.label}</div>
              <div class="fs-3 fw-bold" data-kpi="${kpi.label}">${kpi.value}</div>
            </div>
          </div>
        </div>
      </div>`).join('');
  }

  function renderTable() {
    const all = visibleEmployees();
    const pages = Math.max(1, Math.ceil(all.length / PAGE_SIZE));
    state.page = Math.min(state.page, pages);
    const start = (state.page - 1) * PAGE_SIZE;
    const rows = all.slice(start, start + PAGE_SIZE);

    $('employeeRows').innerHTML = rows.length === 0
      ? `<tr><td colspan="7" class="text-center text-body-secondary py-4"><i class="bi bi-search me-1"></i>No employees match the search and filters.</td></tr>`
      : rows.map((e) => `
        <tr data-id="${e.id}">
          <td class="text-nowrap fw-semibold">${e.id}</td>
          <td class="text-nowrap">${escapeHtml(e.name)}</td>
          <td>${e.department}</td>
          <td class="text-nowrap">${escapeHtml(e.role)}</td>
          <td><span class="badge rounded-pill ${STATUS_BADGES[e.status]}">${e.status}</span></td>
          <td class="text-nowrap">${formatDate(e.joiningDate)}</td>
          <td class="text-end text-nowrap">
            <div class="btn-group btn-group-sm" role="group" aria-label="Actions for ${escapeHtml(e.name)}">
              <button type="button" class="btn btn-outline-primary" data-action="view" title="View" aria-label="View ${escapeHtml(e.name)}"><i class="bi bi-eye"></i></button>
              <button type="button" class="btn btn-outline-secondary" data-action="edit" title="Edit" aria-label="Edit ${escapeHtml(e.name)}"><i class="bi bi-pencil"></i></button>
              <button type="button" class="btn btn-outline-danger" data-action="delete" title="Delete" aria-label="Delete ${escapeHtml(e.name)}"><i class="bi bi-trash"></i></button>
            </div>
          </td>
        </tr>`).join('');

    $('resultCount').textContent = `${all.length} of ${state.employees.length} employees`;
    $('pageInfo').textContent = all.length ? `Showing ${start + 1}–${start + rows.length} of ${all.length}` : 'Showing 0 of 0';
    renderPagination(pages);
    renderSortIcons();
  }

  function renderPagination(pages) {
    const item = (label, page, { disabled = false, active = false, aria = label } = {}) => `
      <li class="page-item${disabled ? ' disabled' : ''}${active ? ' active' : ''}">
        <button type="button" class="page-link" data-page="${page}" aria-label="${aria}" ${active ? 'aria-current="page"' : ''}>${label}</button>
      </li>`;
    let html = item('&laquo;', state.page - 1, { disabled: state.page === 1, aria: 'Previous page' });
    for (let p = 1; p <= pages; p += 1) html += item(String(p), p, { active: p === state.page, aria: `Page ${p}` });
    html += item('&raquo;', state.page + 1, { disabled: state.page === pages, aria: 'Next page' });
    $('pagination').innerHTML = html;
  }

  function renderSortIcons() {
    document.querySelectorAll('.sort').forEach((button) => {
      const active = button.dataset.sort === state.sortBy;
      button.querySelector('.bi').className = 'bi ' + (!active ? 'bi-arrow-down-up text-body-tertiary'
        : state.sortDir === 'asc' ? 'bi-sort-up' : 'bi-sort-down');
      button.closest('th').setAttribute('aria-sort', active ? (state.sortDir === 'asc' ? 'ascending' : 'descending') : 'none');
    });
  }

  function renderDepartmentChart() {
    const total = state.employees.length || 1;
    $('departmentChart').innerHTML = DEPARTMENTS.map((department) => {
      const n = state.employees.filter((e) => e.department === department).length;
      const percent = Math.round((n / total) * 100);
      return `
        <div class="row g-2 align-items-center">
          <div class="col-4 col-sm-3 col-lg-2 small">${department}</div>
          <div class="col">
            <div class="progress" role="progressbar" aria-label="${department}" aria-valuenow="${percent}" aria-valuemin="0" aria-valuemax="100">
              <div class="progress-bar" style="width: ${percent}%"></div>
            </div>
          </div>
          <div class="col-auto small text-body-secondary">${n}</div>
        </div>`;
    }).join('');
  }

  function renderAll() {
    renderKpis();
    renderTable();
    renderDepartmentChart();
  }

  function openForm(employee) {
    form.reset();
    form.classList.remove('was-validated');
    $('editId').value = employee ? employee.id : '';
    $('employeeModalTitle').textContent = employee ? `Edit ${employee.name}` : 'Add Employee';
    $('saveEmployee').textContent = employee ? 'Save changes' : 'Save employee';
    if (employee) {
      $('nameInput').value = employee.name;
      $('departmentInput').value = employee.department;
      $('roleInput').value = employee.role;
      $('statusInput').value = employee.status;
      $('joiningInput').value = employee.joiningDate;
      $('emailInput').value = employee.email || '';
    } else {
      $('statusInput').value = 'Active';
    }
    $('joiningInput').max = today();
    employeeModal.show();
  }

  form.addEventListener('submit', (event) => {
    event.preventDefault();

    ['nameInput', 'roleInput'].forEach((id) => $(id).setCustomValidity($(id).value.trim().length >= 2 ? '' : 'Too short'));
    const joining = $('joiningInput');
    const future = joining.value && joining.value > today();
    joining.setCustomValidity(future ? 'Future date' : '');
    $('joiningFeedback').textContent = future ? 'The joining date cannot be in the future.' : 'Select the joining date.';

    form.classList.add('was-validated');
    if (!form.checkValidity()) return;

    const data = {
      name: $('nameInput').value.trim(),
      department: $('departmentInput').value,
      role: $('roleInput').value.trim(),
      status: $('statusInput').value,
      joiningDate: joining.value,
      email: $('emailInput').value.trim(),
    };
    const editId = $('editId').value;
    if (editId) {
      state.employees = state.employees.map((e) => (e.id === editId ? { ...e, ...data } : e));
      showAlert(`${data.name}'s details were updated.`, 'success');
    } else {
      const id = nextEmployeeId();
      state.employees = [...state.employees, { id, ...data }];
      showAlert(`${data.name} was added as ${id}.`, 'success');
    }
    saveEmployees();
    employeeModal.hide();
    renderAll();
  });

  $('employeeRows').addEventListener('click', (event) => {
    const button = event.target.closest('[data-action]');
    if (!button) return;
    const employee = state.employees.find((e) => e.id === button.closest('tr').dataset.id);

    if (button.dataset.action === 'view') {
      $('detailsTitle').textContent = employee.name;
      $('detailsBody').innerHTML = `
        <dl class="row mb-0">
          <dt class="col-5">Employee ID</dt><dd class="col-7">${employee.id}</dd>
          <dt class="col-5">Department</dt><dd class="col-7">${employee.department}</dd>
          <dt class="col-5">Role</dt><dd class="col-7">${escapeHtml(employee.role)}</dd>
          <dt class="col-5">Status</dt><dd class="col-7"><span class="badge rounded-pill ${STATUS_BADGES[employee.status]}">${employee.status}</span></dd>
          <dt class="col-5">Joining date</dt><dd class="col-7">${formatDate(employee.joiningDate)}</dd>
          <dt class="col-5">Email</dt><dd class="col-7 text-break">${escapeHtml(employee.email || '—')}</dd>
        </dl>`;
      detailsModal.show();
    } else if (button.dataset.action === 'edit') {
      openForm(employee);
    } else {
      state.pendingDeleteId = employee.id;
      $('deleteBody').textContent = `${employee.name} (${employee.id}) will be removed from the list.`;
      deleteModal.show();
    }
  });

  $('confirmDelete').addEventListener('click', () => {
    const employee = state.employees.find((e) => e.id === state.pendingDeleteId);
    state.employees = state.employees.filter((e) => e.id !== state.pendingDeleteId);
    saveEmployees();
    deleteModal.hide();
    showAlert(`${employee.name} was deleted.`, 'danger');
    renderAll();
  });

  $('searchInput').addEventListener('input', (e) => { state.search = e.target.value; state.page = 1; renderTable(); });
  $('departmentFilter').addEventListener('change', (e) => { state.department = e.target.value; state.page = 1; renderTable(); });
  $('statusFilter').addEventListener('change', (e) => { state.status = e.target.value; state.page = 1; renderTable(); });
  $('clearFilters').addEventListener('click', () => {
    state.search = state.department = state.status = '';
    $('searchInput').value = $('departmentFilter').value = $('statusFilter').value = '';
    state.page = 1;
    renderTable();
  });

  document.querySelectorAll('.sort').forEach((button) => button.addEventListener('click', () => {
    const key = button.dataset.sort;
    state.sortDir = state.sortBy === key && state.sortDir === 'asc' ? 'desc' : 'asc';
    state.sortBy = key;
    renderTable();
  }));

  $('pagination').addEventListener('click', (event) => {
    const button = event.target.closest('[data-page]');
    if (!button || button.closest('.disabled')) return;
    state.page = Number(button.dataset.page);
    renderTable();
  });

  $('addEmployeeButton').addEventListener('click', () => openForm(null));

  function applyTheme(theme) {
    document.documentElement.setAttribute('data-bs-theme', theme);
    const button = $('themeToggle');
    button.innerHTML = `<i class="bi ${theme === 'dark' ? 'bi-sun' : 'bi-moon-stars'}"></i>`;
    button.setAttribute('aria-label', theme === 'dark' ? 'Switch to light theme' : 'Switch to dark theme');
  }

  $('themeToggle').addEventListener('click', () => {
    const next = document.documentElement.getAttribute('data-bs-theme') === 'dark' ? 'light' : 'dark';
    applyTheme(next);
    try { localStorage.setItem(THEME_KEY, next); } catch (error) {  }
  });

  $('sidebarCollapse').addEventListener('click', () => {
    const collapsed = $('sidebarColumn').classList.toggle('sidebar--collapsed');
    $('sidebarCollapse').setAttribute('aria-label', collapsed ? 'Expand sidebar' : 'Collapse sidebar');
  });

  document.querySelectorAll('#sidebar .nav-link').forEach((link) => link.addEventListener('click', () => {
    bootstrap.Offcanvas.getInstance($('sidebar'))?.hide();
  }));

  const options = (values) => values.map((v) => `<option value="${v}">${v}</option>`).join('');
  $('departmentFilter').insertAdjacentHTML('beforeend', options(DEPARTMENTS));
  $('statusFilter').insertAdjacentHTML('beforeend', options(STATUSES));
  $('departmentInput').insertAdjacentHTML('beforeend', options(DEPARTMENTS));
  $('statusInput').innerHTML = options(STATUSES);

  let savedTheme = 'light';
  try { savedTheme = localStorage.getItem(THEME_KEY) || 'light'; } catch (error) {  }
  applyTheme(savedTheme);
  renderAll();
})();
