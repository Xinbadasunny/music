import { useState, useRef, useEffect } from 'react'
import { Button, Progress, Typography, Space, message, Upload, Segmented } from 'antd'
import {
  AudioOutlined,
  PauseCircleOutlined,
  CheckCircleOutlined,
  DeleteOutlined,
  PlayCircleOutlined,
  StopOutlined,
  UploadOutlined,
  CloudUploadOutlined
} from '@ant-design/icons'
import type { UploadFile } from 'antd'
import './index.css'

const { Text } = Typography

type InputMode = 'record' | 'upload'

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
  const [inputMode, setInputMode] = useState<InputMode>('record')
  const [isRecording, setIsRecording] = useState(false)
  const [isPaused, setIsPaused] = useState(false)
  const [recordingTime, setRecordingTime] = useState(0)
  const [audioBlob, setAudioBlob] = useState<Blob | null>(null)
  const [audioUrl, setAudioUrl] = useState<string | null>(null)
  const [isPlaying, setIsPlaying] = useState(false)
  const [uploadedFile, setUploadedFile] = useState<UploadFile | null>(null)
  const [audioDuration, setAudioDuration] = useState(0)

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
      const duration = audioDuration > 0 ? audioDuration : recordingTime
      onRecordingComplete(audioBlob, duration)
      message.success(inputMode === 'upload' ? '音频已确认' : '录音已确认')
    }
  }

  const clearRecording = () => {
    if (audioUrl) {
      URL.revokeObjectURL(audioUrl)
    }
    setAudioBlob(null)
    setAudioUrl(null)
    setRecordingTime(0)
    setAudioDuration(0)
    setUploadedFile(null)
    audioChunksRef.current = []
    onRecordingClear?.()
  }

  const handleFileUpload = (file: File) => {
    const allowedTypes = ['audio/mpeg', 'audio/mp3', 'audio/wav', 'audio/webm', 'audio/ogg', 'audio/m4a', 'audio/aac', 'audio/x-m4a']
    const isAudio = allowedTypes.some(type => file.type.includes(type.split('/')[1])) || 
                    file.name.match(/\.(mp3|wav|webm|ogg|m4a|aac|flac)$/i)
    
    if (!isAudio) {
      message.error('请上传音频文件（支持 MP3、WAV、M4A、AAC、OGG、WebM 等格式）')
      return false
    }

    if (file.size > 50 * 1024 * 1024) {
      message.error('文件大小不能超过 50MB')
      return false
    }

    const url = URL.createObjectURL(file)
    const audio = new Audio(url)
    
    audio.onloadedmetadata = () => {
      const duration = Math.round(audio.duration)
      setAudioDuration(duration)
      setRecordingTime(duration)
      setAudioBlob(file)
      setAudioUrl(url)
      setUploadedFile({
        uid: '-1',
        name: file.name,
        status: 'done',
        size: file.size,
      })
      message.success(`已选择: ${file.name}`)
    }

    audio.onerror = () => {
      URL.revokeObjectURL(url)
      message.error('无法读取音频文件，请检查文件格式')
    }

    return false
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

  const handleModeChange = (value: string | number) => {
    if (audioBlob) {
      clearRecording()
    }
    setInputMode(value as InputMode)
  }

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

      {/* 模式切换 */}
      {!isRecording && !audioBlob && (
        <div className="mode-selector">
          <Segmented
            value={inputMode}
            onChange={handleModeChange}
            options={[
              { label: '录制演唱', value: 'record', icon: <AudioOutlined /> },
              { label: '上传音频', value: 'upload', icon: <CloudUploadOutlined /> },
            ]}
            block
            disabled={disabled}
          />
        </div>
      )}

      {/* 录音模式 - 空闲状态 */}
      {inputMode === 'record' && !isRecording && !audioBlob && (
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

      {/* 上传模式 - 空闲状态 */}
      {inputMode === 'upload' && !audioBlob && (
        <div className="upload-area">
          <Upload.Dragger
            accept="audio/*,.mp3,.wav,.m4a,.aac,.ogg,.webm,.flac"
            showUploadList={false}
            beforeUpload={handleFileUpload}
            disabled={disabled}
            className="audio-uploader"
          >
            <p className="upload-icon">
              <UploadOutlined />
            </p>
            <p className="upload-text">点击或拖拽音频文件到此处</p>
            <p className="upload-hint">支持 MP3、WAV、M4A、AAC 等格式，最大 50MB</p>
          </Upload.Dragger>
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

      {/* 完成状态（录音或上传） */}
      {!isRecording && audioBlob && (
        <div className="recorder-complete">
          <div className="recording-result">
            <CheckCircleOutlined className="success-icon" />
            <div className="result-info">
              <Text strong>{inputMode === 'upload' ? '音频已选择' : '录音完成'}</Text>
              {uploadedFile && (
                <Text type="secondary" className="file-name">{uploadedFile.name}</Text>
              )}
              <Text type="secondary">时长: {formatTime(audioDuration > 0 ? audioDuration : recordingTime)}</Text>
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
              {inputMode === 'upload' ? '重选' : '重录'}
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
