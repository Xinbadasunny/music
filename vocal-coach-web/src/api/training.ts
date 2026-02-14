import request from './request'
import type { Course, TrainingProgress, SaveProgressCmd, MultiResponse, ApiResponse } from '../types'

export const trainingApi = {
  getCourses: (): Promise<MultiResponse<Course>> => {
    return request.get('/training/courses')
  },

  getProgress: (): Promise<ApiResponse<Record<string, Record<string, TrainingProgress>>>> => {
    return request.get('/training/progress')
  },

  saveProgress: (data: SaveProgressCmd): Promise<ApiResponse<TrainingProgress>> => {
    return request.post('/training/progress', data)
  },

  getOverallProgress: (): Promise<ApiResponse<number>> => {
    return request.get('/training/overall-progress')
  },

  getCompletedCount: (): Promise<ApiResponse<number>> => {
    return request.get('/training/completed-count')
  },
}
