export interface AnalyzeAudioCmd {
  songName: string
  audioFilePath: string
  referenceAudioPath?: string
}

export interface EvaluationScores {
  overall: number
  pitch: number
  rhythm: number
  voice: number
  breath: number
  style?: number
}

export interface StrengthWeaknessItem {
  dimension: string
  title: string
  description: string
  icon: string
}

export interface Advice {
  dimension: string
  title: string
  description: string
  priority: number
}

export interface CourseRecommendation {
  courseId: string
  courseName: string
  courseIcon: string
  reason: string
  priority: number
}

export interface EvaluationFeatures {
  pitch?: {
    meanPitch?: number
    pitchRange?: number
    pitchStability?: number
    pitchValues?: number[] | null
  }
  rhythm?: {
    tempo?: number
    beatRegularity?: number
    rhythmScore?: number
  }
  voice?: {
    jitter?: number
    shimmer?: number
    hnr?: number
    jitterScore?: number | null
    shimmerScore?: number | null
    hnrScore?: number | null
    voiceScore?: number
    voiceQuality?: string
  }
  timbre?: {
    mfcc?: number[] | null
    brightness?: number
    warmth?: number
    brightnessLevel?: string
  }
  energy?: {
    energyMean?: number
    energyStability?: number
    dynamicRange?: number | null
    breathControlScore?: number
  }
  comparison?: {
    hasReference?: boolean
    dtwDistance?: number
    similarityScore?: number
  } | null
}

export interface EvaluationResult {
  id: number
  songName: string
  audioPath: string
  evaluatedAt: string
  scores: EvaluationScores
  features?: EvaluationFeatures
  strengths: StrengthWeaknessItem[]
  weaknesses: StrengthWeaknessItem[]
  advices: Advice[]
  courseRecommendations: CourseRecommendation[]
  aiEvaluation: string
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
  path: string
  fileName?: string
  fileSize?: number
  uploadTime?: string
}

export interface EvaluationListItem {
  id: number
  songName?: string
  overallScore: number
  createdAt: string
  analysisStatus: 'pending' | 'processing' | 'completed' | 'failed'
}
