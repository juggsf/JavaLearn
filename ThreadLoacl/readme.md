# Login Demo Project

这是一个前后端分离的登录演示项目，使用Vue.js前端和Spring Boot后端。

## 项目结构

```
ThreadLoacl/
├── backend/           # Spring Boot后端
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/demo/
│   │   │   │   ├── config/         # CORS配置
│   │   │   │   ├── controller/     # REST控制器
│   │   │   │   ├── model/          # 实体类
│   │   │   │   ├── service/        # 服务层
│   │   │   │   └── LoginDemoApplication.java
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
└── frontend/          # Vue.js前端
    ├── src/
    │   ├── App.vue
    │   ├── main.js
    │   └── style.css
    ├── index.html
    ├── package.json
    └── vite.config.js
```

## 快速开始

### 后端启动

1. 进入backend目录
2. 安装Maven依赖: `mvn clean install`
3. 启动应用: `mvn spring-boot:run`
4. 后端将在 http://localhost:8080 启动

### 前端启动

1. 进入frontend目录
2. 安装npm依赖: `npm install`
3. 启动开发服务器: `npm run dev`
4. 前端将在 http://localhost:3000 启动

## 功能特性

- 用户登录验证
- 内存中的用户数据存储
- 前后端分离架构
- CORS跨域支持
- 响应式登录界面

## 测试账号

| 用户名 | 密码 |
|--------|------|
| admin  | admin123 |
| user   | user123 |

## API接口

- POST /api/auth/login - 用户登录
- GET /api/auth/users - 获取所有用户列表

## 技术栈

### 后端
- Spring Boot 3.2.0
- Java 17
- Maven

### 前端
- Vue.js 3
- Vite
- Axios
