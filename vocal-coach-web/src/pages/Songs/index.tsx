import { useEffect, useState } from 'react'
import { Input, Tag, Spin, Empty, Card } from 'antd'
import { SearchOutlined, FireOutlined, ClockCircleOutlined } from '@ant-design/icons'
import { useSongStore } from '../../store'

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
    <div className="songs-page">
      {/* 顶部搜索栏 */}
      <div className="search-header">
        <div className="search-title">歌曲库</div>
        <Search
          placeholder="搜索歌曲或歌手"
          allowClear
          enterButton={<SearchOutlined />}
          value={searchValue}
          onChange={(e) => setSearchValue(e.target.value)}
          onSearch={handleSearch}
          className="search-input"
        />
      </div>

      {/* 分类标签横向滚动 */}
      <div className="category-scroll">
        {categories.map((cat) => (
          <div
            key={cat}
            className={`category-tag ${currentCategory === cat ? 'active' : ''}`}
            onClick={() => handleCategoryChange(cat)}
          >
            {cat}
          </div>
        ))}
      </div>

      {/* 歌曲列表卡片式展示 */}
      <Spin spinning={loading} className="loading-container">
        {songs.length === 0 ? (
          <Empty description="暂无歌曲" className="empty-container" />
        ) : (
          <div className="songs-list">
            {songs.map((song) => (
              <Card key={song.id} className="song-card" hoverable>
                <div className="song-header">
                  <div className="song-main-info">
                    <div className="song-name">{song.name}</div>
                    <div className="song-artist">{song.artist}</div>
                  </div>
                  <Tag color={difficultyColors[song.difficulty]} className="difficulty-tag">
                    {difficultyLabels[song.difficulty]}
                  </Tag>
                </div>
                
                <div className="song-meta">
                  <div className="meta-item">
                    <span className="meta-icon">🎵</span>
                    <span className="meta-text">{song.category}</span>
                  </div>
                  <div className="meta-item">
                    <span className="meta-icon">⚡</span>
                    <span className="meta-text">{song.bpm} BPM</span>
                  </div>
                  <div className="meta-item">
                    <span className="meta-icon">🎼</span>
                    <span className="meta-text">{song.key}</span>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        )}
      </Spin>
    </div>
  )
}