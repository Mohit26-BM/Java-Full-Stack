import FormField from '../../shared/FormField.jsx'
import { RELATIONSHIPS } from './bankForm.js'

function NomineeList({ nominees, errors, onAdd, onRemove, onChange }) {
  return (
    <fieldset className="bank__group">
      <legend>Nominees ({nominees.length})</legend>
      {nominees.map((nominee, index) => {
        const nameId = `nominee-${nominee.id}-name`
        const relationId = `nominee-${nominee.id}-relationship`
        return (
          <div key={nominee.id} className="bank__nominee">
            <FormField id={nameId} label={`Nominee ${index + 1} name`} error={errors[nameId]}>
              <input id={nameId} className="input" value={nominee.name}
                onChange={(e) => onChange(nominee.id, 'name', e.target.value)} />
            </FormField>
            <FormField id={relationId} label={`Nominee ${index + 1} relationship`} error={errors[relationId]}>
              <select id={relationId} className="input" value={nominee.relationship}
                onChange={(e) => onChange(nominee.id, 'relationship', e.target.value)}>
                <option value="">Select</option>
                {RELATIONSHIPS.map((r) => <option key={r} value={r}>{r}</option>)}
              </select>
            </FormField>
            <button type="button" className="button button--danger bank__remove"
              onClick={() => onRemove(nominee.id)} aria-label={`Remove nominee ${index + 1}`}>
              Remove
            </button>
          </div>
        )
      })}
      {errors.nominees && <p className="error">{errors.nominees}</p>}
      <button type="button" className="button" onClick={onAdd}>+ Add nominee</button>
    </fieldset>
  )
}

export default NomineeList
