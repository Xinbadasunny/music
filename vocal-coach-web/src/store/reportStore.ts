import { create } from 'zustand'
import { reportApi } from '../api'
import type { Report, Statistics, SaveReportCmd } from '../types'

interface ReportState {
  reports: Report[]
  statistics: Statistics | null
  loading: boolean
  fetchReports: () => Promise<void>
  fetchStatistics: () => Promise<void>
  saveReport: (data: SaveReportCmd) => Promise<Report | null>
  deleteReport: (id: number) => Promise<boolean>
}

export const useReportStore = create<ReportState>((set, get) => ({
  reports: [],
  statistics: null,
  loading: false,

  fetchReports: async () => {
    set({ loading: true })
    try {
      const response = await reportApi.getAll()
      if (response.success && response.data) {
        set({ reports: response.data })
      }
    } catch (error) {
      console.error('Failed to fetch reports:', error)
    } finally {
      set({ loading: false })
    }
  },

  fetchStatistics: async () => {
    try {
      const response = await reportApi.getStatistics()
      if (response.success && response.data) {
        set({ statistics: response.data })
      }
    } catch (error) {
      console.error('Failed to fetch statistics:', error)
    }
  },

  saveReport: async (data: SaveReportCmd) => {
    try {
      const response = await reportApi.save(data)
      if (response.success && response.data) {
        await get().fetchReports()
        return response.data
      }
      return null
    } catch (error) {
      console.error('Failed to save report:', error)
      return null
    }
  },

  deleteReport: async (id: number) => {
    try {
      const response = await reportApi.delete(id)
      if (response.success) {
        await get().fetchReports()
        return true
      }
      return false
    } catch (error) {
      console.error('Failed to delete report:', error)
      return false
    }
  },
}))
