import { useState, useEffect } from 'react'
import { 
  Modal, 
  List, 
  Input, 
  Button, 
  Upload, 
  Typography, 
  Space, 
  Tag, 
  Empty,
  Spin,
  message,
  Tabs
} from 'antd'
import {
  SearchOutlined,
  UploadOutlined,
  CustomerServiceOutlined,
  CheckCircleOutlined
} from '@ant-design/icons'
import type { UploadFile, UploadProps } from 'antd'
import { songApi } from '../../api/song'
import type { Song } from '../../types'
import './index.css'

const { Text, Title } = Typography

interface SongLibraryProps {
  visible: boolean
  onClose: () => void
  onSelect: (song: Song | null, audioFile?: File) => void
  selectedSong?: Song | null
}

export default function SongLibrary({ 
  visible, 
  onClose, 
  onSelect,
  selectedSong 
}: SongLibraryProps) {
  const [songs, setSongs] = useState<Song[]>([])
  const [loading, setLoading] = useState(false)
  const [searchKeyword, setSearchKeyword] = useState('')
  const [uploadFile, setUploadFile] = useState<UploadFile | null>(null)
  const [uploadSongName, setUploadSongName] = useState('')
  const [uploadArtist, setUploadArtist] = useState('')
  const [activeTab, setActiveTab] = useState('library')

  useEffect(() => {
    if (visible) {
      loadSongs()
    }
  }, [visible])

  const loadSongs = async () => {
    setLoading(true)
    try {
      const response = await songApi.getAll()
      if (response.success && response.data) {
        setSongs(response.data)
      }
    } catch (error) {
      console.error('Failed to load songs:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = async () => {
    if (!searchKeyword.trim()) {
      loadSongs()
      return
    }
    setLoading(true)
    try {
      const response = await songApi.search(searchKeyword)
      if (response.success && response.data) {
        setSongs(response.data)
      }
    } catch (error) {
      console.error('Failed to search songs:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleSelectSong = (song: Song) => {
    onSelect(song)
    onClose()
  }

  const beforeUpload = (file: File) => {
    const isAudio = file.type.startsWith('audio/')
    if (!isAudio) {
      message.error('只能上传音频文件！')
      return Upload.LIST_IGNORE
    }
    const isLt50M = file.size / 1024 / 1024 < 50
    if (!isLt50M) {
      message.error('音频文件大小不能超过 50MB！')
      return Upload.LIST_IGNORE
    }
    return false
  }

  const handleUploadChange: UploadProps['onChange'] = ({ fileList }) => {
    if (fileList.length > 0) {
      setUploadFile(fileList[0])
      const fileName = fileList[0].name.replace(/\.[^/.]+$/, '')
      if (!uploadSongName) {
        setUploadSongName(fileName)
      }
    } else {
      setUploadFile(null)
    }
  }

  const handleUploadConfirm = () => {
    if (!uploadFile?.originFileObj) {
      message.warning('请先上传音频文件')
      return
    }
    if (!uploadSongName.trim()) {
      message.warning('请输入歌曲名称')
      return
    }

    const customSong: Song = {
      id: -1,
      name: uploadSongName,
      artist: uploadArtist || '未知',
      difficulty: 2,
      category: '自定义',
      bpm: 0,
      key: '',
      timeSignature: '4/4'
    }

    onSelect(customSong, uploadFile.originFileObj)
    onClose()
    message.success('歌曲已选择，将在评测时上传到乐库')
  }

  const getDifficultyTag = (difficulty: number) => {
    const colors = ['green', 'blue', 'orange', 'red']
    const labels = ['简单', '中等', '困难', '专业']
    const index = Math.min(difficulty - 1, 3)
    return <Tag color={colors[index]}>{labels[index]}</Tag>
  }

  const filteredSongs = songs.filter(song => 
    song.name.toLowerCase().includes(searchKeyword.toLowerCase()) ||
    song.artist.toLowerCase().includes(searchKeyword.toLowerCase())
  )

  const tabItems = [
    {
      key: 'library',
      label: (
        <span>
          <CustomerServiceOutlined />
          乐库选择
        </span>
      ),
      children: (
        <div className="library-content">
          <div className="search-bar">
            <Input
              placeholder="搜索歌曲名或歌手"
              prefix={<SearchOutlined />}
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
              onPressEnter={handleSearch}
              allowClear
            />
            <Button type="primary" onClick={handleSearch}>
              搜索
            </Button>
          </div>

          {loading ? (
            <div className="loading-container">
              <Spin size="large" />
            </div>
          ) : filteredSongs.length === 0 ? (
            <Empty description="暂无歌曲" />
          ) : (
            <List
              className="song-list"
              dataSource={filteredSongs}
              renderItem={(song) => (
                <List.Item
                  className={`song-item ${selectedSong?.id === song.id ? 'selected' : ''}`}
                  onClick={() => handleSelectSong(song)}
                >
                  <div className="song-info">
                    <div className="song-main">
                      <Text strong className="song-name">{song.name}</Text>
                      <Text type="secondary" className="song-artist">{song.artist}</Text>
                    </div>
                    <div className="song-meta">
                      {getDifficultyTag(song.difficulty)}
                      <Tag>{song.category}</Tag>
                    </div>
                  </div>
                  {selectedSong?.id === song.id && (
                    <CheckCircleOutlined className="selected-icon" />
                  )}
                </List.Item>
              )}
            />
          )}
        </div>
      )
    },
    {
      key: 'upload',
      label: (
        <span>
          <UploadOutlined />
          上传新歌
        </span>
      ),
      children: (
        <div className="upload-content">
          <div className="upload-form">
            <div className="form-item">
              <Text strong>歌曲名称 *</Text>
              <Input
                placeholder="请输入歌曲名称"
                value={uploadSongName}
                onChange={(e) => setUploadSongName(e.target.value)}
              />
            </div>

            <div className="form-item">
              <Text strong>歌手/艺术家</Text>
              <Input
                placeholder="请输入歌手名称（可选）"
                value={uploadArtist}
                onChange={(e) => setUploadArtist(e.target.value)}
              />
            </div>

            <div className="form-item">
              <Text strong>原唱音频 *</Text>
              <Upload
                name="audio"
                listType="picture-card"
                className="audio-uploader"
                showUploadList={false}
                beforeUpload={beforeUpload}
                onChange={handleUploadChange}
                accept="audio/*"
              >
                {uploadFile ? (
                  <div className="upload-preview">
                    <CheckCircleOutlined className="success-icon" />
                    <div className="file-name">{uploadFile.name}</div>
                  </div>
                ) : (
                  <div className="upload-placeholder">
                    <UploadOutlined className="upload-icon" />
                    <div>点击上传</div>
                  </div>
                )}
              </Upload>
              {uploadFile && (
                <Button 
                  type="link" 
                  danger 
                  onClick={() => setUploadFile(null)}
                >
                  移除文件
                </Button>
              )}
            </div>

            <Button
              type="primary"
              size="large"
              block
              onClick={handleUploadConfirm}
              disabled={!uploadFile || !uploadSongName.trim()}
            >
              确认选择
            </Button>

            <Text type="secondary" className="upload-hint">
              上传的歌曲将保存到乐库，供所有用户使用
            </Text>
          </div>
        </div>
      )
    }
  ]

  return (
    <Modal
      title={
        <Space>
          <CustomerServiceOutlined />
          <span>选择标准歌曲</span>
        </Space>
      }
      open={visible}
      onCancel={onClose}
      footer={null}
      width={600}
      className="song-library-modal"
    >
      <Tabs 
        activeKey={activeTab} 
        onChange={setActiveTab}
        items={tabItems}
      />
    </Modal>
  )
}
