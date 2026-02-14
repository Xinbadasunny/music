# AI声乐私教 - React前端

基于 React + TypeScript + Vite 的 AI 声乐私教前端应用。

## 技术栈

- React 18
- TypeScript 5
- Vite 5
- Ant Design 5
- Zustand (状态管理)
- React Router 6
- Axios

## 快速开始

### 安装依赖

```bash
cd vocal-coach-web
npm install
```

### 开发模式

```bash
npm run dev
```

应用将在 `http://localhost:3000` 启动。

### 生产构建

```bash
npm run build
```

## 项目结构

```
src/
├── api/           # API 请求层
├── components/    # 通用组件
├── pages/         # 页面组件
├── store/         # Zustand 状态管理
├── styles/        # 样式文件
├── types/         # TypeScript 类型定义
├── App.tsx        # 根组件
└── main.tsx       # 入口文件
```

## 功能模块

- **首页**: 统计概览、训练进度
- **歌曲库**: 歌曲列表、搜索、分类筛选
- **训练课程**: 课程列表、练习进度
- **评测报告**: 报告列表、详情查看
