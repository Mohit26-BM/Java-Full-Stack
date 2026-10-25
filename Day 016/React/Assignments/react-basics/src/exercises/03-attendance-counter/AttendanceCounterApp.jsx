import { useState } from 'react'
import CounterControls from './CounterControls.jsx'
import CounterDisplay from './CounterDisplay.jsx'
import './AttendanceCounter.css'

export const MAX_ATTENDANCE = 50

function AttendanceCounterApp() {
  const [count, setCount] = useState(0)

  const changeBy = (amount) => {
    setCount((current) => Math.min(MAX_ATTENDANCE, Math.max(0, current + amount)))
  }

  const reset = () => setCount(0)

  return (
    <section className="attendance">
      <h1 className="page-title">Attendance Counter</h1>
      <CounterDisplay count={count} max={MAX_ATTENDANCE} />
      <CounterControls count={count} max={MAX_ATTENDANCE} onChange={changeBy} onReset={reset} />
    </section>
  )
}

export default AttendanceCounterApp
