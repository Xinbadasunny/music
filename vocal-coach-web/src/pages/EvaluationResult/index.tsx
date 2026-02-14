import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { Card, Progress, Typography, Button, Tag, Space, Spin, message } from 'antd'
import { 
  ArrowLeftOutlined,
  CheckCircleOutlined, 
  CloseCircleOutlined, 
  ArrowRightOutlined,
  BulbOutlined,
  StarOutlined,
  PlayCircleOutlined,
  CustomerServiceOutlined
} from '@ant-design/icons'
import { evaluationApi } from '../../api/evaluation'
import type { EvaluationResult } from '../../types/evaluation'
import './index.css'

const { Title, Text, Paragraph } = Typography

const dimensionConfig: Record<string, { icon: string; color: string }> = {
  pitch: { icon: '🎯', color: '#667eea' },
  rhythm: { icon: '🥁', color: '#f093fb' },
  voice: { icon: '🔊', color: '#4facfe' },
  breath: { icon: '💨', color: '#43e97b' },
  style: { icon: '✨', color: '#fa709a' }
}

const dimensionNames: Record<string, string> = {
  pitch: '音准',
  rhythm: '节奏',
  voice: '嗓音',
  breath: '气息',
  style: '风格'
}

export default function EvaluationResultPage() {
  const navigate = useNavigate()
  const { id } = useParams()
  const [evaluationData, setEvaluationData] = useState<EvaluationResult | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchData = async () => {
      if (!id) return
      
      setLoading(true)
      try {
        const response = await evaluationApi.getEvaluation(Number(id))
        if (response.success && response.data) {
          setEvaluationData(response.data)
        } else {
          message.error(response.errMessage || '获取评测结果失败')
        }
      } catch (error) {
        console.error('Failed to fetch evaluation:', error)
        message.error('获取评测结果失败')
      } finally {
        setLoading(false)
      }
    }
    fetchData()
  }, [id])

  const handleCourseClick = (courseId: string) => {
    navigate('/training')
  }

  const getScoreColor = (score: number) => {
    if (score >= 90) return '#52c41a'
    if (score >= 80) return '#1890ff'
    if (score >= 60) return '#fa8c16'
    return '#ff4d4f'
  }

  const getScoreLevel = (score: number) => {
    if (score >= 90) return '优秀'
    if (score >= 80) return '良好'
    if (score >= 60) return '及格'
    return '需改进'
  }

  const getScoreGradient = (score: number) => {
    if (score >= 90) return 'linear-gradient(135deg, #52c41a 0%, #73d13d 100%)'
    if (score >= 80) return 'linear-gradient(135deg, #1890ff 0%, #40a9ff 100%)'
    if (score >= 60) return 'linear-gradient(135deg, #fa8c16 0%, #ffa940 100%)'
    return 'linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%)'
  }

  if (loading) {
    return (
      <div className="result-loading">
        <Spin size="large" />
        <Text className="loading-text">加载评测结果...</Text>
      </div>
    )
  }

  if (!evaluationData) {
    return (
      <div className="result-error">
        <Text>无法加载评测结果</Text>
        <Button type="primary" onClick={() => navigate('/')}>返回首页</Button>
      </div>
    )
  }

  const scores = evaluationData.scores || {}
  const overallScore = scores.overall || 0

  return (
    <div className="result-container">
      {/* 顶部导航 */}
      <div className="result-header">
        <Button 
          type="text" 
          icon={<ArrowLeftOutlined />} 
          onClick={() => navigate('/')}
          className="back-btn"
        />
        <Title level={4} className="header-title">评测报告</Title>
        <div style={{ width: 32 }} />
      </div>

      {/* 歌曲信息 */}
      <div className="song-info-banner">
        <CustomerServiceOutlined className="song-icon" />
        <div className="song-details">
          <Text className="song-name">{evaluationData.songName}</Text>
          <Text className="eval-time">
            {evaluationData.evaluatedAt ? new Date(evaluationData.evaluatedAt).toLocaleString('zh-CN') : ''}
          </Text>
        </div>
      </div>

      {/* 综合得分卡片 */}
      <Card className="score-main-card" bordered={false}>
        <div className="score-circle-wrapper">
          <div className="score-circle" style={{ background: getScoreGradient(overallScore) }}>
            <span className="score-number">{Math.round(overallScore)}</span>
            <span className="score-unit">分</span>
          </div>
          <div className="score-level-badge" style={{ background: getScoreGradient(overallScore) }}>
            {getScoreLevel(overallScore)}
          </div>
        </div>
      </Card>

      {/* 各维度得分 */}
      <Card className="dimensions-card" bordered={false}>
        <Title level={5} className="section-title">各维度得分</Title>
        <div className="dimensions-list">
          {Object.entries(scores).filter(([key]) => key !== 'overall').map(([key, value]) => {
            const config = dimensionConfig[key] || { icon: '📊', color: '#1890ff' }
            const score = typeof value === 'number' ? value : 0
            return (
              <div key={key} className="dimension-row">
                <div className="dimension-info">
                  <span className="dimension-icon">{config.icon}</span>
                  <Text className="dimension-name">{dimensionNames[key] || key}</Text>
                </div>
                <div className="dimension-score-area">
                  <Progress 
                    percent={Math.max(0, Math.min(100, score))} 
                    strokeColor={config.color}
                    trailColor="#f0f0f0"
                    showInfo={false}
                    strokeWidth={8}
                    className="dimension-progress"
                  />
                  <Text className="dimension-score" style={{ color: config.color }}>
                    {Math.round(score)}
                  </Text>
                </div>
              </div>
            )
          })}
        </div>
      </Card>

      {/* 优点 */}
      {evaluationData.strengths && evaluationData.strengths.length > 0 && (
        <Card className="feedback-card strengths-card" bordered={false}>
          <div className="card-header">
            <CheckCircleOutlined className="header-icon success" />
            <Title level={5} className="section-title">你的优点</Title>
          </div>
          <div className="feedback-list">
            {evaluationData.strengths.map((item, index) => (
              <div key={index} className="feedback-item strength-item">
                <span className="item-icon">{item.icon}</span>
                <div className="item-content">
                  <Text className="item-title">{item.title}</Text>
                  <Text className="item-desc">{item.description}</Text>
                </div>
              </div>
            ))}
          </div>
        </Card>
      )}

      {/* 待改进 */}
      {evaluationData.weaknesses && evaluationData.weaknesses.length > 0 && (
        <Card className="feedback-card weaknesses-card" bordered={false}>
          <div className="card-header">
            <CloseCircleOutlined className="header-icon warning" />
            <Title level={5} className="section-title">待改进</Title>
          </div>
          <div className="feedback-list">
            {evaluationData.weaknesses.map((item, index) => (
              <div key={index} className="feedback-item weakness-item">
                <span className="item-icon">{item.icon}</span>
                <div className="item-content">
                  <Text className="item-title">{item.title}</Text>
                  <Text className="item-desc">{item.description}</Text>
                </div>
              </div>
            ))}
          </div>
        </Card>
      )}

      {/* 改进建议 */}
      {evaluationData.advices && evaluationData.advices.length > 0 && (
        <Card className="feedback-card advice-card" bordered={false}>
          <div className="card-header">
            <BulbOutlined className="header-icon tip" />
            <Title level={5} className="section-title">改进建议</Title>
          </div>
          <div className="advice-list">
            {evaluationData.advices.map((item, index) => (
              <div key={index} className="advice-item">
                <Tag color="gold" className="advice-tag">{index + 1}</Tag>
                <div className="advice-content">
                  <Text className="advice-title">{item.title}</Text>
                  <Text className="advice-desc">{item.description}</Text>
                </div>
              </div>
            ))}
          </div>
        </Card>
      )}

      {/* AI 评价 */}
      {evaluationData.aiEvaluation && (
        <Card className="ai-card" bordered={false}>
          <div className="card-header">
            <StarOutlined className="header-icon ai" />
            <Title level={5} className="section-title">AI 智能点评</Title>
          </div>
          <Paragraph className="ai-comment">
            {evaluationData.aiEvaluation}
          </Paragraph>
        </Card>
      )}

      {/* 推荐课程 */}
      {evaluationData.courseRecommendations && evaluationData.courseRecommendations.length > 0 && (
        <Card className="courses-card" bordered={false}>
          <div className="card-header">
            <PlayCircleOutlined className="header-icon course" />
            <Title level={5} className="section-title">推荐课程</Title>
          </div>
          <div className="courses-list">
            {evaluationData.courseRecommendations.map((course, index) => (
              <div 
                key={index} 
                className="course-item"
                onClick={() => handleCourseClick(course.courseId)}
              >
                <span className="course-icon">{course.courseIcon}</span>
                <div className="course-info">
                  <Text className="course-name">{course.courseName}</Text>
                  <Text className="course-reason">{course.reason}</Text>
                </div>
                <ArrowRightOutlined className="course-arrow" />
              </div>
            ))}
          </div>
        </Card>
      )}

      {/* 底部操作按钮 */}
      <div className="action-area">
        <Button 
          type="primary" 
          size="large" 
          block
          className="action-btn primary"
          onClick={() => navigate('/evaluate')}
        >
          再测一次
        </Button>
        <Button 
          size="large" 
          block
          className="action-btn secondary"
          onClick={() => navigate('/')}
        >
          返回首页
        </Button>
      </div>
    </div>
  )
}
