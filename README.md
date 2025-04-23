# Stream游戏商城平台

一个基于Spring Boot和原生前端技术构建的游戏商城平台。

## 项目结构

```
Stream游戏商城/
├── 前端/                      # 前端代码
│   ├── *.html                # 页面文件
│   ├── style.css             # 全局样式
│   ├── main.js               # JavaScript主文件
│   └── *.jpg                 # 游戏图片资源
├── stream_game/              # 后端Spring Boot项目
│   ├── src/                  # 源代码
│   ├── pom.xml               # Maven依赖管理
│   └── mvnw                  # Maven包装器
├── run.bat                   # Windows启动脚本
└── run.sh                    # Linux/Mac启动脚本
```

## 功能特性

- 游戏浏览与搜索
- 游戏详情展示
- 购物车管理
- 用户注册与登录
- 游戏交易系统
- 游戏库管理
- 开发者管理

## 技术栈

### 前端
- HTML5
- CSS3
- JavaScript (原生)

### 后端
- Spring Boot 2.7
- Spring Data JPA
- Swagger API文档
- MySQL数据库

## 运行环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- 现代浏览器(Chrome/Firefox/Edge等)

## 快速开始

### 1. 数据库配置

1. 安装MySQL 8.0或更高版本
2. 创建名为"stream"的数据库
3. 配置数据库连接参数:
   - 修改`stream_game/src/main/resources/application.properties`中的数据库连接信息

### 2. 启动服务

#### Windows:
```
run.bat
```

#### Linux/Mac:
```
chmod +x run.sh
./run.sh
```

### 3. 访问应用

浏览器打开: `http://localhost:8080`

API文档: `http://localhost:8080/swagger-ui/index.html`

## 测试账号

- 用户名: testuser
- 密码: password123

## 开发团队

- 前端开发者
- 后端开发者
- UI/UX设计师
- 数据库管理员

## 许可证

[MIT License](LICENSE)
