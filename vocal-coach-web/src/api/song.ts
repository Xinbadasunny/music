import request from './request'
import type { Song, MultiResponse, ApiResponse } from '../types'

export interface UploadSongParams {
  audio: File
  name: string
  artist?: string
  category?: string
  difficulty?: number
}

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

  upload: (params: UploadSongParams): Promise<ApiResponse<Song>> => {
    const formData = new FormData()
    formData.append('audio', params.audio)
    formData.append('name', params.name)
    if (params.artist) {
      formData.append('artist', params.artist)
    }
    if (params.category) {
      formData.append('category', params.category)
    }
    if (params.difficulty !== undefined) {
      formData.append('difficulty', params.difficulty.toString())
    }
    return request.post('/songs/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
  },
}
