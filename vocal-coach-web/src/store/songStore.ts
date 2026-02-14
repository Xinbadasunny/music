import { create } from 'zustand'
import { songApi } from '../api'
import type { Song } from '../types'

interface SongState {
  songs: Song[]
  loading: boolean
  currentCategory: string
  fetchSongs: (category?: string) => Promise<void>
  searchSongs: (keyword: string) => Promise<void>
  setCategory: (category: string) => void
}

export const useSongStore = create<SongState>((set) => ({
  songs: [],
  loading: false,
  currentCategory: '全部',

  fetchSongs: async (category?: string) => {
    set({ loading: true })
    try {
      const response = await songApi.getAll(category)
      if (response.success && response.data) {
        set({ songs: response.data })
      }
    } catch (error) {
      console.error('Failed to fetch songs:', error)
    } finally {
      set({ loading: false })
    }
  },

  searchSongs: async (keyword: string) => {
    set({ loading: true })
    try {
      const response = await songApi.search(keyword)
      if (response.success && response.data) {
        set({ songs: response.data })
      }
    } catch (error) {
      console.error('Failed to search songs:', error)
    } finally {
      set({ loading: false })
    }
  },

  setCategory: (category: string) => {
    set({ currentCategory: category })
  },
}))
