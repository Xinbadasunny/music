import { useEffect } from 'react'
import { Typography, Spin, Empty, Popconfirm, Button } from 'antd'
import { DeleteOutlined, ArrowUpOutlined, ArrowDownOutlined } from '@ant-design/icons'
import { useReportStore } from '../../store'
import type { Report } from '../../types'
import './index.css'

const { Title, Text } = Typography

const getScoreColor = (score: number) => {
  if (score >= 90) return '#52c41a'
  if (score >= 80) return '#1890ff'
  if (score >= 70) return '#fa8c16'
  return '#ff4d4f'
}

const getScoreLevel = (score: number) => {
  if (score >= 90) return '优秀'
  if (score >= 80) return '良好'
  if (score >= 70) return '及格'
  return '需努力'
}

// 圆环进度条组件
const CircularProgress = ({ score, size = 80, strokeWidth = 8 }: { score: number; size?: number; strokeWidth?: number }) => {
  const radius = (size - strokeWidth) / 2
  const circumference = radius * 2 * Math.PI
  const offset = circumference - (score / 100) * circumference
  const color = getScoreColor(score)

  return (
    <div className="circular-progress" style={{ width: size, height: size }}>
      <svg width={size} height={size} style={{ transform: 'rotate(-90deg)' }}>
        <circle
          stroke="#f0f0f0"
          strokeWidth={strokeWidth}
          fill="transparent"
          r={radius}
          cx={size / 2}
          cy={size / 2}
        />
        <circle
          stroke={color}
          strokeWidth={strokeWidth}
          strokeLinecap="round"
          fill="transparent"
          r={radius}
          cx={size / 2}
          cy={size / 2}
          strokeDasharray={circumference}
          strokeDashoffset={offset}
          style={{
            transition: 'stroke-dashoffset 0.5s ease-in-out',
          }}
        />
      </svg>
      <div className="progress-text">
        <span className="score-value" style={{ color }}>{score}</span>
        <span className="score-label">分</span>
      </div>
    </div>
  )
}

// 维度进度条组件
const DimensionBar = ({ label, score }: { label: string; score: number }) => {
  const color = getScoreColor(score)
  
  return (
    <div className="dimension-bar">
      <div className="dimension-header">
        <span className="dimension-label">{label}</span>
        <span className="dimension-score" style={{ color }}>{score}分</span>
      </div>
      <div className="progress-track">
        <div 
          className="progress-fill" 
          style={{ 
            width: `${score}%`,
            backgroundColor: color
          }}
        />
      </div>
    </div>
  )
}

export default function ReportsPage() {
  const { reports, loading, fetchReports, deleteReport } = useReportStore()

  useEffect(() => {
    fetchReports()
  }, [fetchReports])

  const handleDelete = async (id: number) => {
    await deleteReport(id)
  }

  const formatTime = (timestamp: string) => {
    if (!timestamp) return '-'
    const date = new Date(timestamp)
    const now = new Date()
    const diff = now.getTime() - date.getTime()
    const days = Math.floor(diff / (1000 * 60 * 60 * 24))
    
    if (days === 0) {
      return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    } else if (days === 1) {
      return '昨天'
    } else if (days < 7) {
      return `${days}天前`
    } else {
      return date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' })
    }
  }

  return (
    <div className="reports-page">
      <header className="page-header">
        <Title level={4}>评测报告</Title>
        <Text type="secondary" className="report-count">{reports.length} 条记录</Text>
      </header>

      <Spin spinning={loading} tip="加载中...">
        <div className="reports-list">
          {reports.length === 0 ? (
            <Empty 
              description="暂无评测报告" 
              style={{ marginTop: '60px' }}
            />
          ) : (
            reports
              .sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime())
              .map((report) => (
              <div key={report.id} className="report-card">
                <div className="card-header">
                  <div className="song-info">
                    <h3 className="song-name">{report.songName}</h3>
                    <span className="report-time">{formatTime(report.timestamp)}</span>
                  </div>
                  <CircularProgress score={report.overallScore} size={72} strokeWidth={7} />
                </div>

                <div className="score-level">
                  <span className="level-label">评价：</span>
                  <span className="level-value" style={{ color: getScoreColor(report.overallScore) }}>
                    {getScoreLevel(report.overallScore)}
                  </span>
                </div>

                <div className="dimensions-section">
                  <DimensionBar label="音准" score={report.dimensions?.pitch || 0} />
                  <DimensionBar label="节奏" score={report.dimensions?.rhythm || 0} />
                  <DimensionBar label="气息" score={report.dimensions?.breath || 0} />
                  <DimensionBar label="音色" score={report.dimensions?.voice || 0} />
                </div>

                <div className="card-footer">
                  <Popconfirm
                    title="确定删除这条报告吗？"
                    onConfirm={() => handleDelete(report.id)}
                    okText="确定"
                    cancelText="取消"
                  >
                    <Button 
                      type="text" 
                      danger 
                      icon={<DeleteOutlined />}
                      className="delete-button"
                    >
                      删除
                    </Button>
                  </Popconfirm>
                </div>
              </div>
            ))
          )}
        </div>
      </Spin>
    </div>
  )
}