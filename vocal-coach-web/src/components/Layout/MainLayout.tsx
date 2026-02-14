import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import {
  HomeOutlined,
  CustomerServiceOutlined,
  BookOutlined,
  UserOutlined,
} from '@ant-design/icons'
import './MainLayout.css'

const tabItems = [
  { key: '/', icon: <HomeOutlined />, label: '首页' },
  { key: '/songs', icon: <CustomerServiceOutlined />, label: '歌曲库' },
  { key: '/training', icon: <BookOutlined />, label: '训练' },
  { key: '/profile', icon: <UserOutlined />, label: '我的' },
]

export default function MainLayout() {
  const navigate = useNavigate()
  const location = useLocation()

  const isTabBarVisible = ['/', '/songs', '/training', '/profile', '/reports'].includes(location.pathname)

  return (
    <div className="mobile-layout">
      <div className="mobile-content">
        <Outlet />
      </div>
      
      {isTabBarVisible && (
        <div className="mobile-tabbar">
          {tabItems.map((item) => (
            <div
              key={item.key}
              className={`tabbar-item ${location.pathname === item.key ? 'active' : ''}`}
              onClick={() => navigate(item.key)}
            >
              <div className="tabbar-icon">{item.icon}</div>
              <div className="tabbar-label">{item.label}</div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
