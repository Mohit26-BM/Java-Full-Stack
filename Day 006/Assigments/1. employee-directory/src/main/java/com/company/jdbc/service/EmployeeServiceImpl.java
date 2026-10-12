package com.company.jdbc.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.company.jdbc.dao.DepartmentDao;
import com.company.jdbc.dao.EmployeeDao;
import com.company.jdbc.dao.EmployeeSortField;
import com.company.jdbc.exception.DepartmentNotFoundException;
import com.company.jdbc.exception.DuplicateEmployeeException;
import com.company.jdbc.exception.EmployeeNotFoundException;
import com.company.jdbc.exception.InvalidEmployeeException;
import com.company.jdbc.model.Department;
import com.company.jdbc.model.Employee;

public class EmployeeServiceImpl implements EmployeeService {

    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$";

    private static final String CODE_PATTERN = "[A-Z0-9-]+";

    private static final String NAME_PATTERN = "[A-Za-z .'-]+";

    private static final String PHONE_PATTERN = "\\d{10}";

    /*
    Largest value that fits in DECIMAL(12,2).
    */
    private static final BigDecimal MAX_SALARY =
            new BigDecimal("9999999999.99");

    private final EmployeeDao employeeDao;
    private final DepartmentDao departmentDao;

    public EmployeeServiceImpl(EmployeeDao employeeDao,
                               DepartmentDao departmentDao) {

        this.employeeDao = employeeDao;
        this.departmentDao = departmentDao;
    }

    @Override
    public int addEmployee(Employee employee)
            throws InvalidEmployeeException,
                   DepartmentNotFoundException,
                   DuplicateEmployeeException {

        if (employee == null) {
            throw new InvalidEmployeeException(
                    "Employee details cannot be empty."
            );
        }

        /*
        All validation happens BEFORE the INSERT.
        */
        employee.setEmployeeCode(
                validateEmployeeCode(employee.getEmployeeCode()));

        employee.setEmployeeName(
                validateName(employee.getEmployeeName()));

        employee.setEmail(
                validateEmail(employee.getEmail()));

        employee.setPhone(
                validatePhone(employee.getPhone()));

        validateSalary(employee.getSalary());

        validateJoiningDate(employee.getJoiningDate());

        /*
        Check the department here so the user gets a clear message
        instead of a foreign key error from MySQL.
        */
        if (employee.getDepartmentId() == null) {
            throw new InvalidEmployeeException(
                    "Department is required."
            );
        }

        requireDepartment(employee.getDepartmentId());

        int employeeId = employeeDao.add(employee);

        employee.setEmployeeId(employeeId);

        return employeeId;
    }

    @Override
    public Employee findEmployeeById(int employeeId)
            throws EmployeeNotFoundException {

        Employee employee = employeeDao.findById(employeeId);

        if (employee == null) {
            throw new EmployeeNotFoundException(
                    "Employee with ID " + employeeId + " was not found."
            );
        }

        return employee;
    }

    @Override
    public List<Employee> searchByDepartment(int departmentId)
            throws DepartmentNotFoundException {

        requireDepartment(departmentId);

        return employeeDao.findByDepartment(departmentId);
    }

    @Override
    public void updateContact(int employeeId, String email, String phone)
            throws EmployeeNotFoundException,
                   InvalidEmployeeException,
                   DuplicateEmployeeException {

        String validEmail = validateEmail(email);
        String validPhone = validatePhone(phone);

        boolean updated =
                employeeDao.updateContact(employeeId, validEmail, validPhone);

        if (!updated) {
            throw new EmployeeNotFoundException(
                    "Employee with ID " + employeeId
                    + " was not found. Nothing was updated."
            );
        }
    }

    @Override
    public void deleteEmployee(int employeeId)
            throws EmployeeNotFoundException {

        boolean deleted = employeeDao.delete(employeeId);

        if (!deleted) {
            throw new EmployeeNotFoundException(
                    "Employee with ID " + employeeId
                    + " was not found. Nothing was deleted."
            );
        }
    }

    @Override
    public List<Employee> getEmployeePage(EmployeeSortField sortField,
                                          boolean ascending,
                                          int pageNumber,
                                          int pageSize) {

        if (sortField == null) {
            sortField = EmployeeSortField.NAME;
        }

        if (pageNumber < 1) {
            pageNumber = 1;
        }

        if (pageSize < 1) {
            pageSize = 5;
        }

        int offset = (pageNumber - 1) * pageSize;

        return employeeDao.findPage(sortField, ascending, pageSize, offset);
    }

    @Override
    public int getTotalEmployees() {
        return employeeDao.countAll();
    }

    @Override
    public List<Department> getAllDepartments() {
        return departmentDao.findAll();
    }

    // =========================================================
    // VALIDATION HELPERS
    // Each one returns the cleaned (trimmed) value.
    // =========================================================

    private void requireDepartment(int departmentId)
            throws DepartmentNotFoundException {

        if (departmentDao.findById(departmentId) == null) {
            throw new DepartmentNotFoundException(
                    "Department with ID " + departmentId + " does not exist."
            );
        }
    }

    private String validateEmployeeCode(String code)
            throws InvalidEmployeeException {

        if (code == null || code.trim().isEmpty()) {
            throw new InvalidEmployeeException(
                    "Employee code cannot be blank."
            );
        }

        String cleaned = code.trim().toUpperCase();

        if (cleaned.length() > 20) {
            throw new InvalidEmployeeException(
                    "Employee code cannot be longer than 20 characters."
            );
        }

        if (!cleaned.matches(CODE_PATTERN)) {
            throw new InvalidEmployeeException(
                    "Employee code can contain only letters, digits and '-'."
            );
        }

        return cleaned;
    }

    private String validateName(String name)
            throws InvalidEmployeeException {

        if (name == null || name.trim().isEmpty()) {
            throw new InvalidEmployeeException(
                    "Employee name cannot be blank."
            );
        }

        String cleaned = name.trim();

        if (cleaned.length() > 100) {
            throw new InvalidEmployeeException(
                    "Employee name cannot be longer than 100 characters."
            );
        }

        if (!cleaned.matches(NAME_PATTERN)) {
            throw new InvalidEmployeeException(
                    "Employee name can contain only letters, spaces, "
                    + "dots, hyphens and apostrophes."
            );
        }

        return cleaned;
    }

    private String validateEmail(String email)
            throws InvalidEmployeeException {

        if (email == null || email.trim().isEmpty()) {
            throw new InvalidEmployeeException(
                    "Email cannot be blank."
            );
        }

        String cleaned = email.trim().toLowerCase();

        if (cleaned.length() > 120) {
            throw new InvalidEmployeeException(
                    "Email cannot be longer than 120 characters."
            );
        }

        if (!cleaned.matches(EMAIL_PATTERN)) {
            throw new InvalidEmployeeException(
                    "Invalid email address: " + email.trim()
            );
        }

        return cleaned;
    }

    /*
    Phone is optional. Blank input is stored as NULL.
    */
    private String validatePhone(String phone)
            throws InvalidEmployeeException {

        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }

        String cleaned = phone.trim();

        if (!cleaned.matches(PHONE_PATTERN)) {
            throw new InvalidEmployeeException(
                    "Phone number must contain exactly 10 digits."
            );
        }

        return cleaned;
    }

    private void validateSalary(BigDecimal salary)
            throws InvalidEmployeeException {

        if (salary == null) {
            throw new InvalidEmployeeException(
                    "Salary is required."
            );
        }

        if (salary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidEmployeeException(
                    "Salary must be greater than 0."
            );
        }

        if (salary.scale() > 2) {
            throw new InvalidEmployeeException(
                    "Salary can have at most 2 decimal places."
            );
        }

        if (salary.compareTo(MAX_SALARY) > 0) {
            throw new InvalidEmployeeException(
                    "Salary is too large."
            );
        }
    }

    private void validateJoiningDate(LocalDate joiningDate)
            throws InvalidEmployeeException {

        if (joiningDate == null) {
            throw new InvalidEmployeeException(
                    "Joining date is required."
            );
        }

        if (joiningDate.isAfter(LocalDate.now())) {
            throw new InvalidEmployeeException(
                    "Joining date cannot be in the future."
            );
        }
    }
}
