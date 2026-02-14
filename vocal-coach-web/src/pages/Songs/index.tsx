import { useEffect, useState } from 'react'
import { Card, Input, Select, Row, Col, Tag, Typography, Spin, Empty } from 'antd'
import { SearchOutlined } from '@ant-design/icons'
import { useSongStore } from '../../store'

const { Title } = Typography
const { Search } = Input

const categories = ['全部', '儿歌', '经典', '流行']

const difficultyColors: Record<number, string> = {
  1: 'green',
  2: 'blue',
  3: 'orange',
  4: 'red',
  5: 'purple',
}

const difficultyLabels: Record<number, string> = {
  1: '入门',
  2: '简单',
  3: '中等',
  4: '困难',
  5: '专业',
}

export default function SongsPage() {
  const { songs, loading, currentCategory, fetchSongs, searchSongs, setCategory } = useSongStore()
  const [searchValue, setSearchValue] = useState('')

  useEffect(() => {
    fetchSongs(currentCategory === '全部' ? undefined : currentCategory)
  }, [currentCategory, fetchSongs])

  const handleSearch = (value: string) => {
    if (value.trim()) {
      searchSongs(value)
    } else {
      fetchSongs(currentCategory === '全部' ? undefined : currentCategory)
    }
  }

  const handleCategoryChange = (value: string) => {
    setCategory(value)
    setSearchValue('')
  }

  return (
    <div className="page-container">
      <Title level={3}>歌曲库</Title>

      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={12} md={8}>
          <Search
            placeholder="搜索歌曲或歌手"
            allowClear
            enterButton={<SearchOutlined />}
            value={searchValue}
            onChange={(e) => setSearchValue(e.target.value)}
            onSearch={handleSearch}
          />
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Select
            style={{ width: '100%' }}
            value={currentCategory}
            onChange={handleCategoryChange}
            options={categories.map((cat) => ({ label: cat, value: cat }))}
          />
        </Col>
      </Row>

      <Spin spinning={loading}>
        {songs.length === 0 ? (
          <Empty description="暂无歌曲" />
        ) : (
          <Row gutter={[16, 16]}>
            {songs.map((song) => (
              <Col xs={24} sm={12} md={8} lg={6} key={song.id}>
                <Card
                  hoverable
                  title={song.name}
                  extra={
                    <Tag color={difficultyColors[song.difficulty]}>
                      {difficultyLabels[song.difficulty]}
                    </Tag>
                  }
                >
                  <p><strong>歌手：</strong>{song.artist}</p>
                  <p><strong>分类：</strong>{song.category}</p>
                  <p><strong>BPM：</strong>{song.bpm}</p>
                  <p><strong>调式：</strong>{song.key}</p>
                </Card>
              </Col>
            ))}
          </Row>
        )}
      </Spin>
    </div>
  )
}
