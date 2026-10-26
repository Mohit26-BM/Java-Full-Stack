import { useBank } from '../state/BankContext.jsx'

function ProfilePage() {
  const { user, accounts } = useBank()
  return (
    <section className="card narrow">
      <h1 className="page-title">Profile</h1>
      <dl className="details">
        <dt>Name</dt><dd>{user.name}</dd>
        <dt>Customer ID</dt><dd>{user.customerId}</dd>
        <dt>Username</dt><dd>{user.username}</dd>
        <dt>Email</dt><dd>{user.email}</dd>
        <dt>Mobile</dt><dd>{user.phone}</dd>
        <dt>Accounts</dt><dd>{accounts.length}</dd>
      </dl>
    </section>
  )
}

export default ProfilePage
