import request from './request'
import type { Report, Statistics, SaveReportCmd, MultiResponse, ApiResponse } from '../types'

export const reportApi = {
  save: (data: SaveReportCmd): Promise<ApiResponse<Report>> => {
    return request.post('/reports', data)
  },

  getAll: (): Promise<MultiResponse<Report>> => {
    return request.get('/reports')
  },

  getById: (id: number): Promise<ApiResponse<Report>> => {
    return request.get(`/reports/${id}`)
  },

  delete: (id: number): Promise<ApiResponse<void>> => {
    return request.delete(`/reports/${id}`)
  },

  getStatistics: (): Promise<ApiResponse<Statistics>> => {
    return request.get('/reports/statistics')
  },
}
