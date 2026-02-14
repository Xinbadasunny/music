# AI声乐私教 - COLA架构后端

基于 COLA (Clean Object-oriented and Layered Architecture) 架构的 AI 声乐私教后端服务。

## 项目结构

```
vocal-coach-cola/
├── vocal-coach-client/          # 客户端SDK - API定义、DTO、常量
├── vocal-coach-domain/          # 领域层 - 领域实体、领域服务、仓储接口
├── vocal-coach-app/             # 应用层 - 应用服务、命令处理、DTO转换
├── vocal-coach-infrastructure/  # 基础设施层 - 仓储实现、外部服务、配置
├── vocal-coach-adapter/         # 适配层 - Controller、外部接口适配
└── vocal-coach-start/           # 启动模块 - 启动类、配置文件
```

## 技术栈

- Java 11
- Spring Boot 2.7.18
- COLA 4.3.2
- Spring Data JPA
- H2 Database (开发环境)
- Lombok
- MapStruct

## 快速开始

### 编译项目

```bash
cd vocal-coach-cola
mvn clean install
```

### 运行项目

```bash
cd vocal-coach-start
mvn spring-boot:run
```

服务将在 `http://localhost:8080` 启动。

## API 接口

### 歌曲管理
- `GET /api/songs` - 获取所有歌曲
- `GET /api/songs?category=流行` - 按分类筛选
- `GET /api/songs/{id}` - 获取单首歌曲
- `GET /api/songs/search?keyword=xxx` - 搜索歌曲

### 评测报告
- `POST /api/reports` - 保存评测报告
- `GET /api/reports` - 获取所有报告
- `GET /api/reports/{id}` - 获取单个报告
- `DELETE /api/reports/{id}` - 删除报告
- `GET /api/reports/statistics` - 获取统计数据

### 训练进度
- `GET /api/training/courses` - 获取所有课程
- `GET /api/training/progress` - 获取训练进度
- `POST /api/training/progress` - 保存训练进度
- `GET /api/training/overall-progress` - 获取总体进度
- `GET /api/training/completed-count` - 获取已完成数量
