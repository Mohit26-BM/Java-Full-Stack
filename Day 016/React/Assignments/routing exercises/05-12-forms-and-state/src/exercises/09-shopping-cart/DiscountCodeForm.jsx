import { useState } from 'react'
import { DISCOUNT_CODES } from './pricing.js'

function DiscountCodeForm({ applied, error, onApply }) {
  const [input, setInput] = useState('')
  const [unknown, setUnknown] = useState('')

  const handleSubmit = (event) => {
    event.preventDefault()
    const code = input.trim().toUpperCase()
    if (!DISCOUNT_CODES[code]) {
      setUnknown(code ? `"${code}" is not a valid code` : 'Enter a code')
      return
    }
    setUnknown('')
    onApply(code)
    setInput('')
  }

  return (
    <form className="discount" onSubmit={handleSubmit}>
      <label className="field discount__field">
        Discount code
        <span className="discount__row">
          <input className="input" value={input} onChange={(e) => setInput(e.target.value)} placeholder="SAVE10 or FLAT500" />
          <button type="submit" className="button">Apply</button>
        </span>
      </label>
      {unknown && <p className="error" role="alert">{unknown}</p>}
      {applied && !error && (
        <p className="hint">
          Applied {applied}: {DISCOUNT_CODES[applied].description}.{' '}
          <button type="button" className="link-button" onClick={() => onApply('')}>Remove</button>
        </p>
      )}
      {applied && error && <p className="error" role="alert">{error}</p>}
    </form>
  )
}

export default DiscountCodeForm
