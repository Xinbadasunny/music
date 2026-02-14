import { Typography, Card, List, Avatar } from 'antd'
import {
  BarChartOutlined,
  SettingOutlined,
  QuestionCircleOutlined,
  InfoCircleOutlined,
  RightOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import './index.css'

const { Title, Text } = Typography

export default function ProfilePage() {
  const navigate = useNavigate()

  const menuItems = [
    { 
      icon: <BarChartOutlined />, 
      title: '评测报告', 
      subtitle: '查看历史评测记录',
      onClick: () => navigate('/reports')
    },
    { 
      icon: <SettingOutlined />, 
      title: '设置', 
      subtitle: '个性化设置',
      onClick: () => {}
    },
    { 
      icon: <QuestionCircleOutlined />, 
      title: '帮助与反馈', 
      subtitle: '常见问题解答',
      onClick: () => {}
    },
    { 
      icon: <InfoCircleOutlined />, 
      title: '关于', 
      subtitle: 'AI声乐私教 v1.0.0',
      onClick: () => {}
    },
  ]

  return (
    <div className="profile-page">
      <div className="profile-header">
        <Avatar size={80} className="profile-avatar">
          🎤
        </Avatar>
        <Title level={4} className="profile-name">声乐学习者</Title>
        <Text type="secondary">开启你的声乐之旅</Text>
      </div>

      <div className="profile-stats">
        <div className="stat-item">
          <div className="stat-value">0</div>
          <div className="stat-label">评测次数</div>
        </div>
        <div className="stat-divider" />
        <div className="stat-item">
          <div className="stat-value">0</div>
          <div className="stat-label">学习天数</div>
        </div>
        <div className="stat-divider" />
        <div className="stat-item">
          <div className="stat-value">0</div>
          <div className="stat-label">完成课程</div>
        </div>
      </div>

      <Card className="profile-menu" bordered={false}>
        <List
          dataSource={menuItems}
          renderItem={(item) => (
            <List.Item className="menu-item" onClick={item.onClick}>
              <div className="menu-left">
                <div className="menu-icon">{item.icon}</div>
                <div className="menu-content">
                  <Text strong>{item.title}</Text>
                  <Text type="secondary" className="menu-subtitle">{item.subtitle}</Text>
                </div>
              </div>
              <RightOutlined className="menu-arrow" />
            </List.Item>
          )}
        />
      </Card>
    </div>
  )
}
