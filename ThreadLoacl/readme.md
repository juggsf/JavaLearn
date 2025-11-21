# Login Demo Project

这是一个前后端分离的登录演示项目，使用Vue.js前端和Spring Boot后端。
演示threadlocal在线程池环境下不remove的情况下，仍能让旧的value被正常回收
还能用来验证tomcat线程池参数部分设置意义

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



## 测试账号

| 用户名 | 密码 |
|--------|------|
| admin  | admin123 |
| user   | user123 |
