import EmployeeProfileApp from './01-employee-profile-card/EmployeeProfileApp.jsx'
import ProductCatalogApp from './02-product-catalog/ProductCatalogApp.jsx'
import AttendanceCounterApp from './03-attendance-counter/AttendanceCounterApp.jsx'
import EmployeeSearchApp from './04-employee-search/EmployeeSearchApp.jsx'
import TodoApp from './05-todo-list/TodoApp.jsx'
import StudentRegistrationApp from './06-student-registration/StudentRegistrationApp.jsx'
import ShoppingCartApp from './07-shopping-cart/ShoppingCartApp.jsx'
import EmployeeDashboardApp from './08-employee-dashboard/EmployeeDashboardApp.jsx'

export const exercises = [
  {
    path: '01-employee-profile-card',
    title: 'Employee Profile Card',
    concepts: 'Components, JSX, CSS',
    Component: EmployeeProfileApp,
  },
  {
    path: '02-product-catalog',
    title: 'Product Catalog Using Props',
    concepts: 'Props, reusable components',
    Component: ProductCatalogApp,
  },
  {
    path: '03-attendance-counter',
    title: 'Attendance Counter',
    concepts: 'useState, events, conditional rendering',
    Component: AttendanceCounterApp,
  },
  {
    path: '04-employee-search',
    title: 'Employee Search',
    concepts: 'Controlled input, useState, filtering',
    Component: EmployeeSearchApp,
  },
  {
    path: '05-todo-list',
    title: 'To-Do List',
    concepts: 'Arrays in state, map, filter, events',
    Component: TodoApp,
  },
  {
    path: '06-student-registration',
    title: 'Student Registration Form',
    concepts: 'Controlled components, validation',
    Component: StudentRegistrationApp,
  },
  {
    path: '07-shopping-cart',
    title: 'Shopping Cart',
    concepts: 'State, arrays, reduce, derived values',
    Component: ShoppingCartApp,
  },
  {
    path: '08-employee-dashboard',
    title: 'Employee Dashboard',
    concepts: 'Component composition, props, state, derived data',
    Component: EmployeeDashboardApp,
  },
]
