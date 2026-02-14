import { useEffect } from 'react'
import { Card, Row, Col, Statistic, Progress, Typography } from 'antd'
import {
  CustomerServiceOutlined,
  TrophyOutlined,
  RiseOutlined,
  BookOutlined,
} from '@ant-design/icons'
import { useReportStore, useTrainingStore } from '../../store'

const { Title } = Typography

export default function HomePage() {
  const { statistics, fetchStatistics } = useReportStore()
  const { overallProgress, completedCount, fetchOverallProgress } = useTrainingStore()

  useEffect(() => {
    fetchStatistics()
    fetchOverallProgress()
  }, [fetchStatistics, fetchOverallProgress])

  return (
    <div className="page-container">
      <Title level={3}>欢迎使用 AI 声乐私教</Title>
      
      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="练习次数"
              value={statistics?.totalReports || 0}
              prefix={<CustomerServiceOutlined />}
              suffix="次"
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="平均得分"
              value={statistics?.averageScore || 0}
              prefix={<TrophyOutlined />}
              suffix="分"
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="最高得分"
              value={statistics?.bestScore || 0}
              prefix={<RiseOutlined />}
              suffix="分"
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="已完成课程"
              value={completedCount}
              prefix={<BookOutlined />}
              suffix="个"
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24} lg={12}>
          <Card title="训练进度">
            <Progress
              percent={overallProgress}
              status="active"
              strokeColor={{
                '0%': '#108ee9',
                '100%': '#87d068',
              }}
            />
            <p style={{ marginTop: 16, color: '#666' }}>
              继续努力，完成更多训练课程！
            </p>
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card title="维度分析">
            <Row gutter={16}>
              <Col span={12}>
                <Statistic title="音准" value={statistics?.averagePitch || 0} suffix="分" />
              </Col>
              <Col span={12}>
                <Statistic title="节奏" value={statistics?.averageRhythm || 0} suffix="分" />
              </Col>
              <Col span={12} style={{ marginTop: 16 }}>
                <Statistic title="气息" value={statistics?.averageBreath || 0} suffix="分" />
              </Col>
              <Col span={12} style={{ marginTop: 16 }}>
                <Statistic title="音色" value={statistics?.averageVoice || 0} suffix="分" />
              </Col>
            </Row>
          </Card>
        </Col>
      </Row>
    </div>
  )
}
