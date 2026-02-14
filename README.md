# 声乐教练 - 歌曲评测系统

一个基于 AI 的歌曲演唱评测系统，帮助用户提升演唱技巧。

## 系统架构

```
┌─────────────────────────────────────────────────┐
│                   前端 (H5)                      │
│  上传音频 → 查看报告 → 学习课程 → 练习训练        │
└──────────────────────┬──────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────┐
│              后端 (Java COLA 架构)               │
├──────────┬──────────┬───────────┬───────────────┤
│ 音频上传  │ 评测服务  │ 课程服务   │ 训练服务      │
└──────────┴──────────┴───────────┴───────────────┘
       │          │           │
       ▼          ▼           ▼
  ┌────────┐ ┌────────┐ ┌──────────┐
  │Python  │ │Claude  │ │本地文件   │
  │音频分析 │ │AI评价   │ │存储      │
  └────────┘ └────────┘ └──────────┘
```

## 技术栈

### 后端
- Java 8 + Spring Boot 2.7.18
- COLA 架构（多模块）
- Python 音频分析（librosa + parselmouth）
- Claude API（AI 评价生成）
- 本地文件存储

### 前端
- React 18 + TypeScript
- Vite 构建工具
- Ant Design 组件库
- Zustand 状态管理

## 项目结构

```
music/
├── vocal-coach-cola/          # 后端项目
│   ├── vocal-coach-client/    # API 接口定义、DTO
│   ├── vocal-coach-domain/    # 领域实体、仓储接口
│   ├── vocal-coach-app/       # 应用服务
│   ├── vocal-coach-infrastructure/  # 基础设施实现
│   ├── vocal-coach-adapter/   # Controller
│   └── vocal-coach-start/     # 启动模块
├── vocal-coach-web/           # 前端项目
├── scripts/                   # Python 音频分析脚本
└── data/                      # 本地数据存储
    ├── songs.json
    ├── reports.json
    ├── evaluations.json
    └── audios/                # 上传的音频文件
```

## 功能特性

### 1. 歌曲评测
- 上传演唱音频（支持 MP3、WAV 等格式）
- 可选上传参考音频进行对比
- 多维度评分：音准、节奏、嗓音、气息、风格

### 2. AI 智能评价
- 基于 Claude API 生成专业评价
- 分析优点和缺点
- 提供个性化改进建议
- 推荐针对性训练课程

### 3. 课程学习
- 音阶训练、气息训练、节奏训练等
- 每个课程包含多个练习
- 记录学习进度

## 快速开始

### 环境要求
- JDK 8+
- Node.js 18+
- Python 3.8+
- Maven 3.6+

### 安装 Python 依赖
```bash
cd scripts
pip install -r requirements.txt
```

### 启动后端
```bash
cd vocal-coach-cola
mvn clean install
mvn spring-boot:run -pl vocal-coach-start
```

### 启动前端
```bash
cd vocal-coach-web
npm install
npm run dev
```

### 访问
- 前端：http://localhost:5173
- 后端 API：http://localhost:8080

## 配置

### Claude API 配置
在 `application.yml` 中配置：
```yaml
claude:
  api:
    key: your-api-key
    url: https://api.anthropic.com/v1/messages
    model: claude-3-sonnet-20240229
```

### Python 路径配置
```yaml
python:
  path: python3
scripts:
  path: scripts
```

## API 接口

### 评测相关
- `POST /api/evaluation/upload` - 上传音频文件
- `POST /api/evaluation/analyze` - 分析音频并生成评测
- `GET /api/evaluation/list` - 获取评测历史
- `GET /api/evaluation/{id}` - 获取评测详情

### 课程相关
- `GET /api/training/courses` - 获取课程列表
- `GET /api/training/progress` - 获取学习进度
- `POST /api/training/progress` - 保存练习进度

## 评测维度说明

| 维度 | 说明 | 分析方法 |
|------|------|----------|
| 音准 | 音高准确度和稳定性 | librosa 音高提取 |
| 节奏 | 节拍规律性 | librosa 节拍检测 |
| 嗓音 | 声音质量（HNR、Jitter、Shimmer） | parselmouth 分析 |
| 气息 | 气息控制和稳定性 | 能量分析 |
| 风格 | 演唱风格匹配度 | Claude AI 评价 |
