import request from './request'
import type {
  EvaluationResult,
  AudioAnalysisResult,
  AnalyzeAudioCmd,
  UploadAudioResponse,
  EvaluationListItem,
} from '../types/evaluation'
import type { ApiResponse, MultiResponse } from '../types'

export const evaluationApi = {
  uploadAudio: (file: File): Promise<ApiResponse<UploadAudioResponse>> => {
    const formData = new FormData()
    formData.append('file', file)
    
    return request.post('/evaluation/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
  },

  analyzeAudio: (cmd: AnalyzeAudioCmd): Promise<ApiResponse<EvaluationResult>> => {
    return request.post('/evaluation/analyze', cmd)
  },

  listEvaluations: (): Promise<MultiResponse<EvaluationListItem>> => {
    return request.get('/evaluation/list')
  },

  getEvaluation: (id: number): Promise<ApiResponse<EvaluationResult>> => {
    return request.get(`/evaluation/${id}`)
  },

  getAudioAnalysis: (audioId: string): Promise<ApiResponse<AudioAnalysisResult>> => {
    return request.get(`/evaluation/audio/${audioId}`)
  },

  deleteEvaluation: (id: number): Promise<ApiResponse<void>> => {
    return request.delete(`/evaluation/${id}`)
  },
}
