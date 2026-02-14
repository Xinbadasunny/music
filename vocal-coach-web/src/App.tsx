import { BrowserRouter, Routes, Route } from 'react-router-dom'
import MainLayout from './components/Layout/MainLayout'
import HomePage from './pages/Home'
import SongsPage from './pages/Songs'
import TrainingPage from './pages/Training'
import ReportsPage from './pages/Reports'
import EvaluatePage from './pages/Evaluate'
import EvaluationResultPage from './pages/EvaluationResult'
import ProfilePage from './pages/Profile'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<MainLayout />}>
          <Route index element={<HomePage />} />
          <Route path="songs" element={<SongsPage />} />
          <Route path="training" element={<TrainingPage />} />
          <Route path="reports" element={<ReportsPage />} />
          <Route path="evaluate" element={<EvaluatePage />} />
          <Route path="evaluation/:id" element={<EvaluationResultPage />} />
          <Route path="profile" element={<ProfilePage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App