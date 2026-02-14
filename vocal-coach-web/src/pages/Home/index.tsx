import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Typography, Card, List, Tag, Space } from 'antd'
import {
  AudioOutlined,
  CustomerServiceOutlined,
  BookOutlined,
  BarChartOutlined,
  RightOutlined,
  TrophyOutlined,
  FireOutlined,
} from '@ant-design/icons'
import { useReportStore } from '../../store'
import './index.css'

const { Title, Text } = Typography

const features = [
  {
    key: 'evaluate',
    icon: <AudioOutlined />,
    title: '开始评测',
    subtitle: '录制演唱，AI 智能分析',
    color: '#667eea',
    path: '/evaluate'
  },
  {
    key: 'songs',
    icon: <CustomerServiceOutlined />,
    title: '歌曲库',
    subtitle: '海量歌曲任你选',
    color: '#f093fb',
    path: '/songs'
  },
  {
    key: 'training',
    icon: <BookOutlined />,
    title: '训练课程',
    subtitle: '专业课程提升技巧',
    color: '#4facfe',
    path: '/training'
  },
  {
    key: 'reports',
    icon: <BarChartOutlined />,
    title: '评测报告',
    subtitle: '查看历史评测记录',
    color: '#43e97b',
    path: '/reports'
  }
]

export default function HomePage() {
  const navigate = useNavigate()
  const { reports, fetchReports } = useReportStore()
  const [greeting, setGreeting] = useState('')

  useEffect(() => {
    fetchReports()
    
    const updateGreeting = () => {
      const hours = new Date().getHours()
      if (hours < 12) {
        setGreeting('早上好')
      } else if (hours < 18) {
        setGreeting('下午好')
      } else {
        setGreeting('晚上好')
      }
    }
    
    updateGreeting()
    const timer = setInterval(updateGreeting, 60000)
    return () => clearInterval(timer)
  }, [fetchReports])

  const recentReports = reports.slice(0, 2)

  const getScoreColor = (score: number) => {
    if (score >= 90) return '#52c41a'
    if (score >= 80) return '#1890ff'
    if (score >= 60) return '#faad14'
    return '#ff4d4f'
  }

  return (
    <div className="home-page">
      <div className="home-header">
        <div className="header-content">
          <div className="greeting">
            <Text className="greeting-text">{greeting}</Text>
            <Title level={3} className="greeting-title">开启你的声乐之旅</Title>
          </div>
          <div className="header-avatar">🎤</div>
        </div>
      </div>

      <div className="home-content">
        <div className="features-grid">
          {features.map((feature) => (
            <div 
              key={feature.key}
              className="feature-card"
              onClick={() => navigate(feature.path)}
            >
              <div 
                className="feature-icon"
                style={{ background: `linear-gradient(135deg, ${feature.color} 0%, ${feature.color}99 100%)` }}
              >
                {feature.icon}
              </div>
              <div className="feature-info">
                <Text strong className="feature-title">{feature.title}</Text>
                <Text type="secondary" className="feature-subtitle">{feature.subtitle}</Text>
              </div>
              <RightOutlined className="feature-arrow" />
            </div>
          ))}
        </div>

        {recentReports.length > 0 && (
          <Card className="recent-card" bordered={false}>
            <div className="recent-header">
              <Text strong>最近评测</Text>
              <Text 
                type="secondary" 
                className="view-all"
                onClick={() => navigate('/reports')}
              >
                查看全部 <RightOutlined />
              </Text>
            </div>
            <List
              dataSource={recentReports}
              renderItem={(report) => (
                <div className="recent-item">
                  <div className="recent-info">
                    <Text strong>{report.songName}</Text>
                    <Text type="secondary" className="recent-date">
                      {new Date(report.createTime).toLocaleDateString()}
                    </Text>
                  </div>
                  <div className="recent-scores">
                    <Space size="small">
                      <Tag icon={<TrophyOutlined />} color="blue">
                        {report.pitchScore}
                      </Tag>
                      <Tag icon={<FireOutlined />} color="orange">
                        {report.rhythmScore}
                      </Tag>
                    </Space>
                    <div 
                      className="recent-total"
                      style={{ color: getScoreColor(report.totalScore) }}
                    >
                      {report.totalScore}
                    </div>
                  </div>
                </div>
              )}
            />
          </Card>
        )}

        <Card className="tip-card" bordered={false}>
          <div className="tip-content">
            <div className="tip-icon">💡</div>
            <div className="tip-text">
              <Text strong>今日小贴士</Text>
              <Text type="secondary">唱歌前先做好热身，可以有效保护嗓子哦~</Text>
            </div>
          </div>
        </Card>
      </div>
    </div>
  )
}