import { useState } from 'react'
import { EyeIcon, EyeOffIcon, LockIcon } from './Icons.jsx'

export default function PasswordInput({ id, ...props }) {
  const [visible, setVisible] = useState(false)

  return (
    <div className="input-icon has-toggle">
      <LockIcon className="lead-icon" />
      <input id={id} type={visible ? 'text' : 'password'} className="form-control" {...props} />
      <button
        type="button"
        className="toggle-visibility"
        onClick={() => setVisible((v) => !v)}
        aria-label={visible ? 'Hide password' : 'Show password'}
        aria-pressed={visible}
      >
        {visible ? <EyeOffIcon /> : <EyeIcon />}
      </button>
    </div>
  )
}
