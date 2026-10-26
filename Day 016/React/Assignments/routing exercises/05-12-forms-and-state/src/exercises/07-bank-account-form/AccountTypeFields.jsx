import FormField from '../../shared/FormField.jsx'
import { MIN_BALANCE_OPTIONS } from './bankForm.js'

function AccountTypeFields({ form, errors, onChange }) {
  if (form.accountType === 'Savings') {
    return (
      <fieldset className="bank__group">
        <legend>Savings account</legend>
        <FormField id="minimumBalance" label="Minimum balance preference" error={errors.minimumBalance}>
          <select id="minimumBalance" className="input" value={form.minimumBalance}
            onChange={(e) => onChange('minimumBalance', e.target.value)}>
            <option value="">Select a preference</option>
            {MIN_BALANCE_OPTIONS.map((o) => <option key={o} value={o}>{o}</option>)}
          </select>
        </FormField>
      </fieldset>
    )
  }
  if (form.accountType === 'Current') {
    return (
      <fieldset className="bank__group">
        <legend>Current account</legend>
        <FormField id="businessName" label="Business name" error={errors.businessName}>
          <input id="businessName" className="input" value={form.businessName}
            onChange={(e) => onChange('businessName', e.target.value)} />
        </FormField>
      </fieldset>
    )
  }
  return null
}

export default AccountTypeFields
