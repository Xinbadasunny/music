import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { 
  Card, 
  Button, 
  Typography, 
  Space, 
  Progress, 
  message,
  Spin,
  Divider
} from 'antd'
import {
  ArrowLeftOutlined,
  CustomerServiceOutlined,
  CheckCircleOutlined,
  LoadingOutlined,
  PlusOutlined
} from '@ant-design/icons'
import AudioRecorder from '../../components/AudioRecorder'
import SongLibrary from '../../components/SongLibrary'
import { evaluationApi } from '../../api/evaluation'
import type { Song } from '../../types'
import './index.css'

const { Title, Text } = Typography

export default function EvaluatePage() {
  const navigate = useNavigate()
  const [loading, setLoading] = useState(false)
  const [evaluating, setEvaluating] = useState(false)
  const [progress, setProgress] = useState(0)
  const [progressText, setProgressText] = useState('')
  
  const [recordingBlob, setRecordingBlob] = useState<Blob | null>(null)
  const [recordingDuration, setRecordingDuration] = useState(0)
  const [recordingConfirmed, setRecordingConfirmed] = useState(false)
  
  const [selectedSong, setSelectedSong] = useState<Song | null>(null)
  const [customSongFile, setCustomSongFile] = useState<File | null>(null)
  const [showSongLibrary, setShowSongLibrary] = useState(false)

  const handleBack = () => {
    navigate(-1)
  }

  const handleRecordingComplete = (blob: Blob, duration: number) => {
    setRecordingBlob(blob)
    setRecordingDuration(duration)
    setRecordingConfirmed(true)
  }

  const handleRecordingClear = () => {
    setRecordingBlob(null)
    setRecordingDuration(0)
    setRecordingConfirmed(false)
  }

  const handleSongSelect = (song: Song | null, audioFile?: File) => {
    setSelectedSong(song)
    if (audioFile) {
      setCustomSongFile(audioFile)
    } else {
      setCustomSongFile(null)
    }
  }

  const clearSelectedSong = () => {
    setSelectedSong(null)
    setCustomSongFile(null)
  }

  const updateProgress = (percent: number, text: string) => {
    setProgress(percent)
    setProgressText(text)
  }

  const handleSubmit = async () => {
    if (!recordingConfirmed || !recordingBlob) {
      message.warning('请先完成录音并确认')
      return
    }

    if (!selectedSong) {
      message.warning('请选择标准歌曲')
      return
    }

    setLoading(true)
    setEvaluating(true)

    try {
      updateProgress(10, '正在上传录音...')
      
      const recordingFile = new File([recordingBlob], `recording_${Date.now()}.webm`, {
        type: 'audio/webm'
      })
      const uploadResponse = await evaluationApi.uploadAudio(recordingFile)
      
      if (!uploadResponse.success) {
        throw new Error('录音上传失败')
      }

      updateProgress(30, '正在处理标准歌曲...')
      
      let referenceAudioPath = ''
      if (customSongFile) {
        const refUploadResponse = await evaluationApi.uploadAudio(customSongFile)
        if (refUploadResponse.success && refUploadResponse.data) {
          referenceAudioPath = refUploadResponse.data.path
        }
      }

      updateProgress(50, '正在分析音频...')
      
      const analyzeResponse = await evaluationApi.analyzeAudio({
        songName: selectedSong.name,
        audioFilePath: uploadResponse.data?.path || '',
        referenceAudioPath: referenceAudioPath
      })

      updateProgress(70, '正在生成 AI 评价...')
      await new Promise(resolve => setTimeout(resolve, 1000))

      updateProgress(90, '正在生成报告...')
      await new Promise(resolve => setTimeout(resolve, 500))

      updateProgress(100, '评测完成！')

      if (analyzeResponse.success && analyzeResponse.data) {
        message.success('评测完成！')
        navigate(`/evaluation/${analyzeResponse.data.id}`)
      } else {
        throw new Error(analyzeResponse.errMessage || '评测失败')
      }

    } catch (error: any) {
      console.error('Evaluation failed:', error)
      message.error(error.message || '评测失败，请重试')
    } finally {
      setLoading(false)
      setEvaluating(false)
      setProgress(0)
      setProgressText('')
    }
  }

  const canSubmit = recordingConfirmed && selectedSong

  return (
    <div className="evaluate-page-container">
      <div className="evaluate-header">
        <Button 
          type="text" 
          icon={<ArrowLeftOutlined />} 
          onClick={handleBack}
          className="back-button"
        >
          返回
        </Button>
        <Title level={3} className="page-title">歌曲评测</Title>
        <div style={{ width: 60 }} />
      </div>

      <div className="evaluate-content">
        <Card className="evaluate-card" bordered={false}>
          <div className="section">
            <div className="section-header">
              <Title level={5}>第一步：选择标准歌曲</Title>
              <Text type="secondary">从乐库选择或上传新歌曲</Text>
            </div>
            
            {selectedSong ? (
              <div className="selected-song">
                <div className="song-info">
                  <CustomerServiceOutlined className="song-icon" />
                  <div className="song-details">
                    <Text strong>{selectedSong.name}</Text>
                    <Text type="secondary">{selectedSong.artist}</Text>
                  </div>
                  <CheckCircleOutlined className="check-icon" />
                </div>
                <Button type="link" onClick={clearSelectedSong}>
                  重新选择
                </Button>
              </div>
            ) : (
              <Button
                type="dashed"
                size="large"
                icon={<PlusOutlined />}
                onClick={() => setShowSongLibrary(true)}
                className="select-song-button"
                block
              >
                点击选择歌曲
              </Button>
            )}
          </div>

          <Divider />

          <div className="section">
            <div className="section-header">
              <Title level={5}>第二步：录制演唱</Title>
              <Text type="secondary">点击开始录音，录完后点击确认</Text>
            </div>
            
            <AudioRecorder
              onRecordingComplete={handleRecordingComplete}
              onRecordingClear={handleRecordingClear}
              disabled={evaluating}
            />
            
            {recordingConfirmed && (
              <div className="recording-status">
                <CheckCircleOutlined className="status-icon" />
                <Text>录音已确认，时长 {Math.floor(recordingDuration / 60)}:{(recordingDuration % 60).toString().padStart(2, '0')}</Text>
              </div>
            )}
          </div>

          <Divider />

          {evaluating && (
            <div className="evaluation-progress">
              <div className="progress-header">
                <Space>
                  <Spin indicator={<LoadingOutlined spin />} />
                  <Text strong>{progressText || '正在评测中...'}</Text>
                </Space>
                <Text type="secondary">{progress}%</Text>
              </div>
              <Progress 
                percent={progress} 
                status="active"
                strokeColor={{
                  '0%': '#667eea',
                  '100%': '#764ba2',
                }}
                className="progress-bar"
              />
            </div>
          )}

          <Button
            type="primary"
            size="large"
            onClick={handleSubmit}
            loading={loading || evaluating}
            disabled={!canSubmit}
            className="submit-button"
            block
          >
            {evaluating ? '评测中...' : '开始评测'}
          </Button>

          {!canSubmit && (
            <div className="submit-hint">
              <Text type="secondary">
                {!selectedSong && '请先选择标准歌曲'}
                {selectedSong && !recordingConfirmed && '请完成录音并确认'}
              </Text>
            </div>
          )}
        </Card>

        <Card className="tips-card" bordered={false}>
          <Title level={5} className="tips-title">评测提示</Title>
          <ul className="tips-list">
            <li>请在安静的环境中录音</li>
            <li>建议录制完整的歌曲片段</li>
            <li>选择标准歌曲可提高评测准确度</li>
            <li>上传的歌曲会保存到乐库供所有人使用</li>
          </ul>
        </Card>
      </div>

      <SongLibrary
        visible={showSongLibrary}
        onClose={() => setShowSongLibrary(false)}
        onSelect={handleSongSelect}
        selectedSong={selectedSong}
      />
    </div>
  )
}
