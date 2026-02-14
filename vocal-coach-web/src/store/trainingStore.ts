import { create } from 'zustand'
import { trainingApi } from '../api'
import type { Course, TrainingProgress, SaveProgressCmd } from '../types'

interface TrainingState {
  courses: Course[]
  progress: Record<string, Record<string, TrainingProgress>>
  overallProgress: number
  completedCount: number
  loading: boolean
  fetchCourses: () => Promise<void>
  fetchProgress: () => Promise<void>
  fetchOverallProgress: () => Promise<void>
  saveProgress: (data: SaveProgressCmd) => Promise<TrainingProgress | null>
}

export const useTrainingStore = create<TrainingState>((set, get) => ({
  courses: [],
  progress: {},
  overallProgress: 0,
  completedCount: 0,
  loading: false,

  fetchCourses: async () => {
    set({ loading: true })
    try {
      const response = await trainingApi.getCourses()
      if (response.success && response.data) {
        set({ courses: response.data })
      }
    } catch (error) {
      console.error('Failed to fetch courses:', error)
    } finally {
      set({ loading: false })
    }
  },

  fetchProgress: async () => {
    try {
      const response = await trainingApi.getProgress()
      if (response.success && response.data) {
        set({ progress: response.data })
      }
    } catch (error) {
      console.error('Failed to fetch progress:', error)
    }
  },

  fetchOverallProgress: async () => {
    try {
      const [progressRes, countRes] = await Promise.all([
        trainingApi.getOverallProgress(),
        trainingApi.getCompletedCount(),
      ])
      if (progressRes.success && progressRes.data !== undefined) {
        set({ overallProgress: progressRes.data })
      }
      if (countRes.success && countRes.data !== undefined) {
        set({ completedCount: countRes.data })
      }
    } catch (error) {
      console.error('Failed to fetch overall progress:', error)
    }
  },

  saveProgress: async (data: SaveProgressCmd) => {
    try {
      const response = await trainingApi.saveProgress(data)
      if (response.success && response.data) {
        await get().fetchProgress()
        await get().fetchOverallProgress()
        return response.data
      }
      return null
    } catch (error) {
      console.error('Failed to save progress:', error)
      return null
    }
  },
}))
