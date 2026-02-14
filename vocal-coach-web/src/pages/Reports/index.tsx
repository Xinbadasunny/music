import { useEffect } from 'react'
import { Card, Table, Tag, Typography, Spin, Empty, Popconfirm, Button, Space } from 'antd'
import { DeleteOutlined } from '@ant-design/icons'
import type { ColumnsType } from 'antd/es/table'
import { useReportStore } from '../../store'
import type { Report } from '../../types'

const { Title } = Typography

const getScoreColor = (score: number) => {
  if (score >= 90) return 'green'
  if (score >= 80) return 'blue'
  if (score >= 70) return 'orange'
  return 'red'
}

export default function ReportsPage() {
  const { reports, loading, fetchReports, deleteReport } = useReportStore()

  useEffect(() => {
    fetchReports()
  }, [fetchReports])

  const handleDelete = async (id: number) => {
    await deleteReport(id)
  }

  const columns: ColumnsType<Report> = [
    {
      title: '歌曲',
      dataIndex: 'songName',
      key: 'songName',
    },
    {
      title: '总分',
      dataIndex: 'overallScore',
      key: 'overallScore',
      render: (score: number) => (
        <Tag color={getScoreColor(score)} style={{ fontSize: 16 }}>
          {score}分
        </Tag>
      ),
      sorter: (a, b) => a.overallScore - b.overallScore,
    },
    {
      title: '音准',
      key: 'pitch',
      render: (_, record) => `${record.dimensions?.pitch || 0}分`,
    },
    {
      title: '节奏',
      key: 'rhythm',
      render: (_, record) => `${record.dimensions?.rhythm || 0}分`,
    },
    {
      title: '气息',
      key: 'breath',
      render: (_, record) => `${record.dimensions?.breath || 0}分`,
    },
    {
      title: '音色',
      key: 'voice',
      render: (_, record) => `${record.dimensions?.voice || 0}分`,
    },
    {
      title: '时间',
      dataIndex: 'timestamp',
      key: 'timestamp',
      render: (timestamp: string) => {
        if (!timestamp) return '-'
        const date = new Date(timestamp)
        return date.toLocaleString('zh-CN')
      },
      sorter: (a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime(),
      defaultSortOrder: 'descend',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space>
          <Popconfirm
            title="确定删除这条报告吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <div className="page-container">
      <Title level={3}>评测报告</Title>

      <Card>
        <Spin spinning={loading}>
          {reports.length === 0 ? (
            <Empty description="暂无评测报告" />
          ) : (
            <Table
              columns={columns}
              dataSource={reports}
              rowKey="id"
              pagination={{
                pageSize: 10,
                showSizeChanger: true,
                showTotal: (total) => `共 ${total} 条记录`,
              }}
            />
          )}
        </Spin>
      </Card>
    </div>
  )
}
