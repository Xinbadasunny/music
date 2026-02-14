import request from './request'
import type { Song, MultiResponse, ApiResponse } from '../types'

export const songApi = {
  getAll: (category?: string): Promise<MultiResponse<Song>> => {
    const params = category ? { category } : {}
    return request.get('/songs', { params })
  },

  getById: (id: number): Promise<ApiResponse<Song>> => {
    return request.get(`/songs/${id}`)
  },

  search: (keyword: string): Promise<MultiResponse<Song>> => {
    return request.get('/songs/search', { params: { keyword } })
  },
}
