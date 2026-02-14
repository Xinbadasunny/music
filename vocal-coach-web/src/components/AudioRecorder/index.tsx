import { useState, useRef, useEffect } from 'react'
import { Button, Progress, Typography, Space, message } from 'antd'
import {
  AudioOutlined,
  PauseCircleOutlined,
  CheckCircleOutlined,
  DeleteOutlined,
  PlayCircleOutlined,
  StopOutlined
} from '@ant-design/icons'
import './index.css'

const { Text } = Typography

interface AudioRecorderProps {
  onRecordingComplete: (blob: Blob, duration: number) => void
  onRecordingClear?: () => void
  disabled?: boolean
}

export default function AudioRecorder({ 
  onRecordingComplete, 
  onRecordingClear,
  disabled = false 
}: AudioRecorderProps) {
  const [isRecording, setIsRecording] = useState(false)
  const [isPaused, setIsPaused] = useState(false)
  const [recordingTime, setRecordingTime] = useState(0)
  const [audioBlob, setAudioBlob] = useState<Blob | null>(null)
  const [audioUrl, setAudioUrl] = useState<string | null>(null)
  const [isPlaying, setIsPlaying] = useState(false)

  const mediaRecorderRef = useRef<MediaRecorder | null>(null)
  const audioChunksRef = useRef<Blob[]>([])
  const timerRef = useRef<NodeJS.Timeout | null>(null)
  const audioRef = useRef<HTMLAudioElement | null>(null)
  const streamRef = useRef<MediaStream | null>(null)

  useEffect(() => {
    return () => {
      if (timerRef.current) {
        clearInterval(timerRef.current)
      }
      if (audioUrl) {
        URL.revokeObjectURL(audioUrl)
      }
      if (streamRef.current) {
        streamRef.current.getTracks().forEach(track => track.stop())
      }
    }
  }, [audioUrl])

  const formatTime = (seconds: number): string => {
    const mins = Math.floor(seconds / 60)
    const secs = seconds % 60
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  }

  const startRecording = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ 
        audio: {
          echoCancellation: true,
          noiseSuppression: true,
          sampleRate: 44100
        } 
      })
      streamRef.current = stream

      const mediaRecorder = new MediaRecorder(stream, {
        mimeType: 'audio/webm;codecs=opus'
      })
      mediaRecorderRef.current = mediaRecorder
      audioChunksRef.current = []

      mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          audioChunksRef.current.push(event.data)
        }
      }

      mediaRecorder.onstop = () => {
        const blob = new Blob(audioChunksRef.current, { type: 'audio/webm' })
        setAudioBlob(blob)
        const url = URL.createObjectURL(blob)
        setAudioUrl(url)
        
        stream.getTracks().forEach(track => track.stop())
      }

      mediaRecorder.start(100)
      setIsRecording(true)
      setIsPaused(false)
      setRecordingTime(0)

      timerRef.current = setInterval(() => {
        setRecordingTime(prev => prev + 1)
      }, 1000)

      message.success('开始录音')
    } catch (error) {
      console.error('Failed to start recording:', error)
      message.error('无法访问麦克风，请检查权限设置')
    }
  }

  const pauseRecording = () => {
    if (mediaRecorderRef.current && isRecording) {
      if (isPaused) {
        mediaRecorderRef.current.resume()
        timerRef.current = setInterval(() => {
          setRecordingTime(prev => prev + 1)
        }, 1000)
        setIsPaused(false)
      } else {
        mediaRecorderRef.current.pause()
        if (timerRef.current) {
          clearInterval(timerRef.current)
        }
        setIsPaused(true)
      }
    }
  }

  const stopRecording = () => {
    if (mediaRecorderRef.current && isRecording) {
      mediaRecorderRef.current.stop()
      if (timerRef.current) {
        clearInterval(timerRef.current)
      }
      setIsRecording(false)
      setIsPaused(false)
      message.success('录音完成')
    }
  }

  const confirmRecording = () => {
    if (audioBlob) {
      onRecordingComplete(audioBlob, recordingTime)
      message.success('录音已确认')
    }
  }

  const clearRecording = () => {
    if (audioUrl) {
      URL.revokeObjectURL(audioUrl)
    }
    setAudioBlob(null)
    setAudioUrl(null)
    setRecordingTime(0)
    audioChunksRef.current = []
    onRecordingClear?.()
  }

  const playRecording = () => {
    if (audioUrl && audioRef.current) {
      if (isPlaying) {
        audioRef.current.pause()
        audioRef.current.currentTime = 0
        setIsPlaying(false)
      } else {
        audioRef.current.play()
        setIsPlaying(true)
      }
    }
  }

  const handleAudioEnded = () => {
    setIsPlaying(false)
  }

  const maxRecordingTime = 300

  return (
    <div className="audio-recorder">
      {audioUrl && (
        <audio 
          ref={audioRef} 
          src={audioUrl} 
          onEnded={handleAudioEnded}
          style={{ display: 'none' }}
        />
      )}

      {!isRecording && !audioBlob && (
        <div className="recorder-idle">
          <Button
            type="primary"
            size="large"
            icon={<AudioOutlined />}
            onClick={startRecording}
            disabled={disabled}
            className="record-button"
          >
            点击开始录音
          </Button>
          <Text type="secondary" className="recorder-hint">
            最长录音时间 5 分钟
          </Text>
        </div>
      )}

      {isRecording && (
        <div className="recorder-recording">
          <div className="recording-indicator">
            <div className={`pulse-dot ${isPaused ? 'paused' : ''}`} />
            <Text strong className="recording-time">
              {formatTime(recordingTime)}
            </Text>
          </div>
          
          <Progress 
            percent={(recordingTime / maxRecordingTime) * 100} 
            showInfo={false}
            strokeColor="#ff4d4f"
            className="recording-progress"
          />

          <Space size="middle" className="recording-controls">
            <Button
              type="default"
              icon={isPaused ? <PlayCircleOutlined /> : <PauseCircleOutlined />}
              onClick={pauseRecording}
            >
              {isPaused ? '继续' : '暂停'}
            </Button>
            <Button
              type="primary"
              danger
              icon={<StopOutlined />}
              onClick={stopRecording}
            >
              停止录音
            </Button>
          </Space>
        </div>
      )}

      {!isRecording && audioBlob && (
        <div className="recorder-complete">
          <div className="recording-result">
            <CheckCircleOutlined className="success-icon" />
            <div className="result-info">
              <Text strong>录音完成</Text>
              <Text type="secondary">时长: {formatTime(recordingTime)}</Text>
            </div>
          </div>

          <Space size="middle" className="result-controls">
            <Button
              type="default"
              icon={isPlaying ? <StopOutlined /> : <PlayCircleOutlined />}
              onClick={playRecording}
            >
              {isPlaying ? '停止' : '试听'}
            </Button>
            <Button
              type="default"
              danger
              icon={<DeleteOutlined />}
              onClick={clearRecording}
            >
              重录
            </Button>
            <Button
              type="primary"
              icon={<CheckCircleOutlined />}
              onClick={confirmRecording}
            >
              确认使用
            </Button>
          </Space>
        </div>
      )}
    </div>
  )
}
