import EmployeeRegistrationApp from './05-employee-registration/EmployeeRegistrationApp.jsx'
import ValidatedFormApp from './06-validated-form/ValidatedFormApp.jsx'
import BankAccountFormApp from './07-bank-account-form/BankAccountFormApp.jsx'
import LoanApplicationApp from './08-loan-application/LoanApplicationApp.jsx'
import ShoppingCartApp from './09-shopping-cart/ShoppingCartApp.jsx'
import StudentResultsApp from './10-student-results/StudentResultsApp.jsx'
import PreferencesApp from './11-preferences-context/PreferencesApp.jsx'
import BankReducerApp from './12-bank-reducer/BankReducerApp.jsx'

export const exercises = [
  {
    number: 5,
    path: '05-employee-registration',
    title: 'Employee Registration Form',
    concepts: 'Controlled inputs, form submission',
    Component: EmployeeRegistrationApp,
  },
  {
    number: 6,
    path: '06-validated-form',
    title: 'Employee Form with Validation',
    concepts: 'Client-side validation, field-level errors',
    Component: ValidatedFormApp,
  },
  {
    number: 7,
    path: '07-bank-account-form',
    title: 'Dynamic Bank Account Form',
    concepts: 'Conditional fields, dynamic nominee entries',
    Component: BankAccountFormApp,
  },
  {
    number: 8,
    path: '08-loan-application',
    title: 'Multi-Step Loan Application',
    concepts: 'Multi-step form, per-step validation',
    Component: LoanApplicationApp,
  },
  {
    number: 9,
    path: '09-shopping-cart',
    title: 'Shopping Cart with useState',
    concepts: 'Array state, derived totals, discount codes',
    Component: ShoppingCartApp,
  },
  {
    number: 10,
    path: '10-student-results',
    title: 'Lift State Up — Student Results',
    concepts: 'Shared state in a common parent, props, callbacks',
    Component: StudentResultsApp,
  },
  {
    number: 11,
    path: '11-preferences-context',
    title: 'Employee Preferences Using Context API',
    concepts: 'createContext, useContext, localStorage',
    Component: PreferencesApp,
  },
  {
    number: 12,
    path: '12-bank-reducer',
    title: 'Bank Transaction Manager with useReducer',
    concepts: 'useReducer, actions, pure state transitions',
    Component: BankReducerApp,
  },
]
