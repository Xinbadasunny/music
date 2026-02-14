import { BrowserRouter, Routes, Route } from 'react-router-dom'
import MainLayout from './components/Layout/MainLayout'
import HomePage from './pages/Home'
import SongsPage from './pages/Songs'
import TrainingPage from './pages/Training'
import ReportsPage from './pages/Reports'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<MainLayout />}>
          <Route index element={<HomePage />} />
          <Route path="songs" element={<SongsPage />} />
          <Route path="training" element={<TrainingPage />} />
          <Route path="reports" element={<ReportsPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
