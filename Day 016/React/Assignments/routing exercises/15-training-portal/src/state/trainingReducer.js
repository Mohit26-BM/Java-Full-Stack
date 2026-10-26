import { sampleData } from './sampleData.js'

const nextId = (items, prefix, start) =>
  `${prefix}-${Math.max(start, ...items.map((item) => Number(item.id.split('-')[1]))) + 1}`

export const enrolledCount = (enrollments, courseId) =>
  enrollments.filter((e) => e.courseId === courseId && e.status !== 'Cancelled').length

export function enrollmentProblem(state, participantId, courseId) {
  const course = state.courses.find((c) => c.id === courseId)
  if (!state.participants.some((p) => p.id === participantId)) return 'Select a participant'
  if (!course) return 'Select a course'
  if (state.enrollments.some((e) => e.participantId === participantId && e.courseId === courseId)) {
    return 'This participant is already enrolled in this course'
  }
  if (enrolledCount(state.enrollments, courseId) >= course.capacity) return 'This course is full'
  return ''
}

export function trainingReducer(state, action) {
  switch (action.type) {
    case 'ADD_PARTICIPANT':
      return { ...state, participants: [...state.participants, { ...action.participant, id: action.id }] }

    case 'ADD_COURSE':
      return { ...state, courses: [...state.courses, { ...action.course, id: action.id }] }

    case 'ENROLL': {
      if (enrollmentProblem(state, action.participantId, action.courseId)) return state
      const enrollment = {
        id: nextId(state.enrollments, 'E', 300),
        participantId: action.participantId,
        courseId: action.courseId,
        date: action.date,
        status: 'Enrolled',
      }
      return { ...state, enrollments: [...state.enrollments, enrollment] }
    }

    case 'COMPLETE_ENROLLMENT':
      return {
        ...state,
        enrollments: state.enrollments.map((e) => (e.id === action.id ? { ...e, status: 'Completed' } : e)),
      }

    case 'CANCEL_ENROLLMENT':
      return { ...state, enrollments: state.enrollments.filter((e) => e.id !== action.id) }

    case 'RESET_DATA':
      return sampleData

    default:
      return state
  }
}

export const newParticipantId = (state) => nextId(state.participants, 'P', 100)
export const newCourseId = (state) => nextId(state.courses, 'C', 200)
