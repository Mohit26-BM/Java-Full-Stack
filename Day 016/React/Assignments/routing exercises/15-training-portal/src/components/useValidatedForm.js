import { useState } from 'react'

export function useValidatedForm(initial, validate) {
  const [values, setValues] = useState(initial)
  const [touched, setTouched] = useState({})
  const errors = validate(values)
  const isValid = Object.keys(errors).length === 0

  const fieldProps = (name) => ({
    name,
    value: values[name],
    onChange: (event) => setValues({ ...values, [name]: event.target.value }),
    onBlur: () => setTouched({ ...touched, [name]: true }),
    error: touched[name] ? errors[name] : '',
  })

  const touchAll = () => setTouched(Object.fromEntries(Object.keys(initial).map((k) => [k, true])))

  return { values, isValid, fieldProps, touchAll }
}
