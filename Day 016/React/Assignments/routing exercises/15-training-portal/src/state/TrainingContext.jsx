import { createContext, useContext, useEffect, useReducer } from 'react'
import { sampleData } from './sampleData.js'
import { newCourseId, newParticipantId, trainingReducer } from './trainingReducer.js'

export const STORAGE_KEY = 'training-portal-data'
const KEYS = ['trainers', 'participants', 'courses', 'enrollments']

export function loadData() {
  try {
    const saved = JSON.parse(localStorage.getItem(STORAGE_KEY) ?? 'null')
    if (saved && KEYS.every((key) => Array.isArray(saved[key]))) return saved
  } catch {

  }
  return sampleData
}

const TrainingContext = createContext(null)

export function TrainingProvider({ children }) {
  const [state, dispatch] = useReducer(trainingReducer, undefined, loadData)

  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state))
    } catch {

    }
  }, [state])

  const today = () => new Date().toISOString().slice(0, 10)
  const actions = {
    addParticipant: (participant) => {
      const id = newParticipantId(state)
      dispatch({ type: 'ADD_PARTICIPANT', participant, id })
      return id
    },
    addCourse: (course) => {
      const id = newCourseId(state)
      dispatch({ type: 'ADD_COURSE', course, id })
      return id
    },
    enroll: (participantId, courseId) => dispatch({ type: 'ENROLL', participantId, courseId, date: today() }),
    completeEnrollment: (id) => dispatch({ type: 'COMPLETE_ENROLLMENT', id }),
    cancelEnrollment: (id) => dispatch({ type: 'CANCEL_ENROLLMENT', id }),
    resetData: () => dispatch({ type: 'RESET_DATA' }),
  }

  const lookups = {
    trainerById: (id) => state.trainers.find((t) => t.id === id),
    participantById: (id) => state.participants.find((p) => p.id === id),
    courseById: (id) => state.courses.find((c) => c.id === id),
  }

  return (
    <TrainingContext.Provider value={{ state, ...actions, ...lookups }}>
      {children}
    </TrainingContext.Provider>
  )
}

export function useTraining() {
  const context = useContext(TrainingContext)
  if (!context) throw new Error('useTraining must be used inside <TrainingProvider>')
  return context
}
