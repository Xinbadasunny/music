export interface MelodyNote {
  note: number
  duration: number
  lyric: string
}

export interface Song {
  id: number
  name: string
  artist: string
  difficulty: number
  category: string
  bpm: number
  key: string
  timeSignature: string
  melodyPattern: MelodyNote[]
}

export interface Dimensions {
  pitch: number
  rhythm: number
  breath: number
  voice: number
}

export interface Suggestion {
  type: string
  title: string
  description: string
  icon: string
}

export interface TrainingRecommendation {
  courseId: string
  exerciseId: string
  reason: string
}

export interface Report {
  id: number
  songName: string
  overallScore: number
  dimensions: Dimensions
  suggestions: Suggestion[]
  trainingRecommendations: TrainingRecommendation[]
  timestamp: string
}

export interface Exercise {
  id: string
  name: string
  description: string
  bpm: number
  notes: number[]
  passingScore: number
  tips: string
}

export interface Course {
  id: string
  name: string
  icon: string
  description: string
  exercises: Exercise[]
}

export interface TrainingProgress {
  id: number
  courseId: string
  exerciseId: string
  bestScore: number
  attempts: number
  completed: boolean
  lastPracticeTime: string
}

export interface Statistics {
  totalReports: number
  averageScore: number
  bestScore: number
  worstScore: number
  averagePitch: number
  averageRhythm: number
  averageBreath: number
  averageVoice: number
  improvementTrend: number
  recentTrend: string
}

export interface ApiResponse<T> {
  success: boolean
  errCode?: string
  errMessage?: string
  data?: T
}

export interface MultiResponse<T> {
  success: boolean
  errCode?: string
  errMessage?: string
  data?: T[]
}

export interface SaveReportCmd {
  songName: string
  overallScore: number
  dimensions: Dimensions
  suggestions?: Suggestion[]
  trainingRecommendations?: TrainingRecommendation[]
}

export interface SaveProgressCmd {
  courseId: string
  exerciseId: string
  score: number
}
