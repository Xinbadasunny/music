import { useEffect, useState } from 'react'
import { Card, Progress, Tag, Typography, Spin, Empty, Modal, List, Button, Space, Badge, Drawer } from 'antd'
import { CheckCircleOutlined, PlayCircleOutlined, ClockCircleOutlined, TrophyOutlined, LockOutlined, ArrowLeftOutlined } from '@ant-design/icons'
import { useTrainingStore } from '../../store'
import type { Course, Exercise } from '../../types'
import './index.css'

const { Title, Text, Paragraph } = Typography

interface CourseProgress {
  completed: number
  total: number
  percentage: number
}

interface ExerciseStatus {
  completed: boolean
  bestScore?: number
  locked?: boolean
}

export default function TrainingPage() {
  const { courses, progress, loading, fetchCourses, fetchProgress } = useTrainingStore()
  const [selectedCourse, setSelectedCourse] = useState<Course | null>(null)
  const [drawerVisible, setDrawerVisible] = useState(false)

  useEffect(() => {
    fetchCourses()
    fetchProgress()
  }, [fetchCourses, fetchProgress])

  const getCourseProgress = (course: Course): CourseProgress => {
    const courseProgress = progress[course.id] || {}
    const completedCount = Object.values(courseProgress).filter((p) => p.completed).length
    const totalCount = course.exercises?.length || 0
    return {
      completed: completedCount,
      total: totalCount,
      percentage: totalCount > 0 ? Math.round((completedCount / totalCount) * 100) : 0
    }
  }

  const getExerciseStatus = (courseId: string, exerciseId: string, index: number): ExerciseStatus => {
    const status = progress[courseId]?.[exerciseId]
    if (status) {
      return status
    }
    return {
      completed: false,
      locked: index > 0
    }
  }

  const handleCourseClick = (course: Course) => {
    setSelectedCourse(course)
    setDrawerVisible(true)
  }

  const handleStartExercise = (exercise: Exercise) => {
    console.log('开始练习:', exercise.name)
  }

  const getCourseLevel = (percentage: number) => {
    if (percentage === 100) return { text: '已完成', color: '#52c41a', icon: <TrophyOutlined /> }
    if (percentage >= 50) return { text: '进行中', color: '#1890ff', icon: <PlayCircleOutlined /> }
    return { text: '未开始', color: '#8c8c8c', icon: <ClockCircleOutlined /> }
  }

  return (
    <div className="training-page-container">
      {/* 移动端顶部标题栏 */}
      <div className="mobile-header">
        <Title level={3}>课程学习</Title>
        <Text type="secondary">系统化学习声乐技巧</Text>
      </div>

      <Spin spinning={loading}>
        {courses.length === 0 ? (
          <Empty 
            description="暂无课程" 
            style={{ padding: '60px 0' }}
          />
        ) : (
          <div className="course-list">
            {courses.map((course) => {
              const courseProgress = getCourseProgress(course)
              const level = getCourseLevel(courseProgress.percentage)
              return (
                <Card
                  key={course.id}
                  className="course-card-mobile"
                  onClick={() => handleCourseClick(course)}
                >
                  <div className="course-card-inner">
                    {/* 左侧图标区域 */}
                    <div className="course-icon-section">
                      <div className="course-icon-bg">
                        <span className="course-icon">{course.icon}</span>
                      </div>
                      <Badge 
                        count={level.text} 
                        style={{ 
                          backgroundColor: level.color,
                          marginTop: 8,
                          padding: '2px 8px',
                          borderRadius: '8px',
                          fontSize: 11
                        }}
                      />
                    </div>

                    {/* 右侧内容区域 */}
                    <div className="course-info-section">
                      <div className="course-header">
                        <Title level={4} className="course-title">{course.name}</Title>
                        <Paragraph 
                          type="secondary" 
                          className="course-description"
                          ellipsis={{ rows: 1 }}
                        >
                          {course.description}
                        </Paragraph>
                      </div>

                      <div className="course-stats">
                        <Space size="small">
                          <Text type="secondary" className="stat-text">
                            <ClockCircleOutlined /> {courseProgress.completed}/{courseProgress.total}
                          </Text>
                        </Space>
                      </div>

                      <div className="course-progress-section">
                        <div className="progress-header">
                          <Text className="progress-label">进度</Text>
                          <Text strong className="progress-percent">{courseProgress.percentage}%</Text>
                        </div>
                        <Progress 
                          percent={courseProgress.percentage} 
                          strokeColor={{
                            '0%': '#667eea',
                            '100%': '#764ba2',
                          }}
                          showInfo={false}
                          strokeWidth={6}
                          className="progress-bar"
                        />
                      </div>
                    </div>

                    {/* 右侧箭头 */}
                    <div className="course-arrow">
                      <PlayCircleOutlined />
                    </div>
                  </div>
                </Card>
              )
            })}
          </div>
        )}
      </Spin>

      {/* 移动端全屏抽屉 */}
      <Drawer
        title={null}
        placement="right"
        onClose={() => setDrawerVisible(false)}
        open={drawerVisible}
        width="100%"
        className="course-drawer-mobile"
        closable={false}
        styles={{
          body: { padding: 0, background: '#f5f7fa' }
        }}
      >
        {selectedCourse && (
          <div className="drawer-content">
            {/* 抽屉头部 */}
            <div className="drawer-header">
              <Button 
                type="text" 
                icon={<ArrowLeftOutlined />} 
                onClick={() => setDrawerVisible(false)}
                className="back-button"
              >
                返回
              </Button>
              <div className="course-header-info">
                <div className="course-icon-large">{selectedCourse.icon}</div>
                <div className="course-title-section">
                  <Title level={4} className="drawer-course-title">{selectedCourse.name}</Title>
                  <Text type="secondary" className="drawer-course-desc">{selectedCourse.description}</Text>
                </div>
              </div>
            </div>

            {/* 练习列表 */}
            <div className="exercise-list-mobile">
              <div className="exercise-list-header">
                <Text strong>课程练习</Text>
                <Tag color="blue">{selectedCourse.exercises?.length || 0} 个练习</Tag>
              </div>
              
              <List
                className="exercise-items-mobile"
                dataSource={selectedCourse.exercises || []}
                renderItem={(exercise: Exercise, index: number) => {
                  const status = getExerciseStatus(selectedCourse.id, exercise.id, index)
                  return (
                    <Card 
                      className={`exercise-card-mobile ${status.completed ? 'completed' : ''} ${status.locked ? 'locked' : ''}`}
                    >
                      <div className="exercise-card-inner">
                        <div className="exercise-left">
                          <div className="exercise-number-mobile">
                            {index + 1}
                          </div>
                          <div className="exercise-content">
                            <div className="exercise-title-row">
                              <Text strong className="exercise-name-mobile">{exercise.name}</Text>
                              {status.completed && (
                                <TrophyOutlined className="trophy-icon" />
                              )}
                            </div>
                            <Paragraph 
                              type="secondary" 
                              className="exercise-desc-mobile"
                              ellipsis={{ rows: 2 }}
                            >
                              {exercise.description}
                            </Paragraph>
                            <Space size="small" className="exercise-meta">
                              <Text type="secondary" className="meta-text">
                                <ClockCircleOutlined /> {exercise.duration || '5'}分钟
                              </Text>
                              <Text type="secondary" className="meta-text">
                                及格: {exercise.passingScore}分
                              </Text>
                            </Space>
                          </div>
                        </div>
                        <div className="exercise-right">
                          {status.locked ? (
                            <div className="status-badge locked">
                              <LockOutlined />
                              <Text>未解锁</Text>
                            </div>
                          ) : status.completed ? (
                            <div className="status-badge completed">
                              <CheckCircleOutlined />
                              <Text>{status.bestScore}分</Text>
                            </div>
                          ) : (
                            <Button 
                              type="primary" 
                              icon={<PlayCircleOutlined />} 
                              size="small"
                              className="start-button"
                              onClick={() => handleStartExercise(exercise)}
                            >
                              开始
                            </Button>
                          )}
                        </div>
                      </div>
                    </Card>
                  )
                }}
              />
            </div>
          </div>
        )}
      </Drawer>
    </div>
  )
}