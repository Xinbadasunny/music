import type { Dimensions } from './index'

export interface AnalyzeAudioCmd {
  audioId: string
  songId?: number
  evaluationType?: 'basic' | 'advanced' | 'professional'
}

export interface StrengthsWeaknesses {
  pitch: string[]
  rhythm: string[]
  breath: string[]
  voice: string[]
}

export interface Advice {
  category: string
  title: string
  description: string
  actionableSteps: string[]
  priority: 'high' | 'medium' | 'low'
}

export interface CourseRecommendation {
  courseId: string
  courseName: string
  exerciseId: string
  exerciseName: string
  reason: string
  difficulty: string
  estimatedDuration: number
}

export interface EvaluationResult {
  id: number
  audioId: string
  songId?: number
  songName?: string
  overallScore: number
  dimensions: Dimensions
  strengths: StrengthsWeaknesses
  weaknesses: StrengthsWeaknesses
  advices: Advice[]
  courseRecommendations: CourseRecommendation[]
  createdAt: string
  updatedAt: string
}

export interface AudioAnalysisResult {
  audioId: string
  duration: number
  sampleRate: number
  channels: number
  format: string
  fileSize: number
  uploadTime: string
  analysisStatus: 'pending' | 'processing' | 'completed' | 'failed'
  errorMessage?: string
}

export interface UploadAudioResponse {
  audioId: string
  fileName: string
  fileSize: number
  uploadTime: string
}

export interface EvaluationListItem {
  id: number
  songName?: string
  overallScore: number
  createdAt: string
  analysisStatus: 'pending' | 'processing' | 'completed' | 'failed'
}
