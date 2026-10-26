export default function EmployeeCard({ employee }) {
  const { firstName, lastName, email, phone, image, company } = employee

  return (
    <div className="card h-100 shadow-sm">
      <div className="card-body">
        <div className="d-flex align-items-center gap-3 mb-3">
          {image && <img src={image} alt="" width="56" height="56" className="rounded-circle bg-light flex-shrink-0" />}
          <div>
            <h2 className="h5 mb-0">
              {firstName} {lastName}
            </h2>
            <div className="text-secondary small">{company?.title ?? 'N/A'}</div>
          </div>
        </div>
        <dl className="row small mb-0">
          <dt className="col-4">Email</dt>
          <dd className="col-8 text-break">{email}</dd>
          <dt className="col-4">Phone</dt>
          <dd className="col-8">{phone}</dd>
          <dt className="col-4">Department</dt>
          <dd className="col-8">{company?.department ?? 'N/A'}</dd>
          <dt className="col-4">Company</dt>
          <dd className="col-8 mb-0">{company?.name ?? 'N/A'}</dd>
        </dl>
      </div>
    </div>
  )
}
