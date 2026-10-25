import { useEffect, useState } from 'react'
import { formatMoney } from '../format.js'

const reduced = () => window.matchMedia?.('(prefers-reduced-motion: reduce)').matches

/** Animates a currency amount from 0 to value. */
export default function CountUp({ value, duration = 900 }) {
  const [shown, setShown] = useState(0)

  useEffect(() => {
    if (reduced()) {
      const id = requestAnimationFrame(() => setShown(value))
      return () => cancelAnimationFrame(id)
    }
    let frame
    const start = performance.now()
    const tick = (now) => {
      const t = Math.min((now - start) / duration, 1)
      setShown(value * (1 - Math.pow(1 - t, 3)))
      if (t < 1) frame = requestAnimationFrame(tick)
    }
    frame = requestAnimationFrame(tick)
    return () => cancelAnimationFrame(frame)
  }, [value, duration])

  return <>{formatMoney(shown)}</>
}
