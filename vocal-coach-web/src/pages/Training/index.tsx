import { useEffect, useState } from 'react'
import { Card, Row, Col, Progress, Tag, Typography, Spin, Empty, Modal, List, Button } from 'antd'
import { CheckCircleOutlined, PlayCircleOutlined } from '@ant-design/icons'
import { useTrainingStore } from '../../store'
import type { Course, Exercise } from '../../types'

const { Title, Text } = Typography

export default function TrainingPage() {
  const { courses, progress, loading, fetchCourses, fetchProgress } = useTrainingStore()
  const [selectedCourse, setSelectedCourse] = useState<Course | null>(null)
  const [modalVisible, setModalVisible] = useState(false)

  useEffect(() => {
    fetchCourses()
    fetchProgress()
  }, [fetchCourses, fetchProgress])

  const getCourseProgress = (course: Course) => {
    const courseProgress = progress[course.id] || {}
    const completedCount = Object.values(courseProgress).filter((p) => p.completed).length
    const totalCount = course.exercises?.length || 0
    return totalCount > 0 ? Math.round((completedCount / totalCount) * 100) : 0
  }

  const getExerciseStatus = (courseId: string, exerciseId: string) => {
    return progress[courseId]?.[exerciseId]
  }

  const handleCourseClick = (course: Course) => {
    setSelectedCourse(course)
    setModalVisible(true)
  }

  return (
    <div className="page-container">
      <Title level={3}>训练课程</Title>

      <Spin spinning={loading}>
        {courses.length === 0 ? (
          <Empty description="暂无课程" />
        ) : (
          <Row gutter={[16, 16]}>
            {courses.map((course) => {
              const progressPercent = getCourseProgress(course)
              return (
                <Col xs={24} sm={12} md={8} key={course.id}>
                  <Card
                    hoverable
                    onClick={() => handleCourseClick(course)}
                    title={
                      <span>
                        <span style={{ fontSize: 24, marginRight: 8 }}>{course.icon}</span>
                        {course.name}
                      </span>
                    }
                  >
                    <p>{course.description}</p>
                    <Progress percent={progressPercent} size="small" />
                    <Text type="secondary" style={{ marginTop: 8, display: 'block' }}>
                      {course.exercises?.length || 0} 个练习
                    </Text>
                  </Card>
                </Col>
              )
            })}
          </Row>
        )}
      </Spin>

      <Modal
        title={
          selectedCourse && (
            <span>
              <span style={{ fontSize: 24, marginRight: 8 }}>{selectedCourse.icon}</span>
              {selectedCourse.name}
            </span>
          )
        }
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        footer={null}
        width={600}
      >
        {selectedCourse && (
          <List
            dataSource={selectedCourse.exercises || []}
            renderItem={(exercise: Exercise) => {
              const status = getExerciseStatus(selectedCourse.id, exercise.id)
              return (
                <List.Item
                  actions={[
                    status?.completed ? (
                      <Tag color="success" icon={<CheckCircleOutlined />}>
                        已完成 ({status.bestScore}分)
                      </Tag>
                    ) : (
                      <Button type="primary" icon={<PlayCircleOutlined />} size="small">
                        开始练习
                      </Button>
                    ),
                  ]}
                >
                  <List.Item.Meta
                    title={exercise.name}
                    description={
                      <>
                        <p>{exercise.description}</p>
                        <Text type="secondary">
                          BPM: {exercise.bpm} | 及格分: {exercise.passingScore}
                        </Text>
                      </>
                    }
                  />
                </List.Item>
              )
            }}
          />
        )}
      </Modal>
    </div>
  )
}
