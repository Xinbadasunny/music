import { useState } from 'react'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import { Layout, Menu } from 'antd'
import {
  HomeOutlined,
  CustomerServiceOutlined,
  BookOutlined,
  BarChartOutlined,
} from '@ant-design/icons'

const { Header, Content, Sider } = Layout

const menuItems = [
  { key: '/', icon: <HomeOutlined />, label: '首页' },
  { key: '/songs', icon: <CustomerServiceOutlined />, label: '歌曲库' },
  { key: '/training', icon: <BookOutlined />, label: '训练课程' },
  { key: '/reports', icon: <BarChartOutlined />, label: '评测报告' },
]

export default function MainLayout() {
  const [collapsed, setCollapsed] = useState(false)
  const navigate = useNavigate()
  const location = useLocation()

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider collapsible collapsed={collapsed} onCollapse={setCollapsed}>
        <div style={{ 
          height: 64, 
          display: 'flex', 
          alignItems: 'center', 
          justifyContent: 'center',
          color: '#fff',
          fontSize: collapsed ? 16 : 20,
          fontWeight: 'bold'
        }}>
          {collapsed ? '🎤' : '🎤 AI声乐私教'}
        </div>
        <Menu
          theme="dark"
          selectedKeys={[location.pathname]}
          mode="inline"
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header style={{ 
          padding: '0 24px', 
          background: '#fff',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
        }}>
          <h2 style={{ margin: 0 }}>AI声乐私教</h2>
        </Header>
        <Content>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  )
}
