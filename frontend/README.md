# 权限管理平台 - 前端

## 技术栈

- Vue 3
- Element Plus
- Vue Router
- Pinia
- Axios
- Vite

## 安装依赖

```bash
cd frontend
npm install
```

## 启动开发服务器

```bash
npm run dev
```

访问地址：http://localhost:3000

## 构建生产版本

```bash
npm run build
```

## 项目结构

```
frontend/
├── src/
│   ├── views/          # 页面组件
│   │   ├── Login.vue   # 登录页
│   │   ├── Register.vue # 注册页
│   │   ├── modules/      # 模块管理
│   │   ├── permissions/ # 权限配置
│   │   ├── roles/      # 角色管理
│   │   ├── options/    # 选项管理
│   │   └── accounts/   # 账号管理
│   ├── layouts/        # 布局组件
│   │   └── MainLayout.vue # 主布局（左侧导航树 + 右侧内容）
│   ├── stores/         # Pinia 状态管理
│   ├── router/          # 路由配置
│   └── utils/          # 工具函数
│       └── api.js      # API 封装
├── index.html
├── vite.config.js
└── package.json
```

## 功能说明

1. **登录/注册**：用户登录和注册功能
2. **主布局**：左侧系统功能模块树导航，右侧内容区域
3. **模块管理**：树形结构展示，支持添加、编辑、删除模块
4. **权限配置**：选择角色，配置模块权限（可浏览、可新增、可修改、可删除等）
5. **角色管理**：角色的增删改查
6. **选项管理**：系统选项的增删改查（岗位、片区等）
7. **账号管理**：审核注册、重置密码、分配角色

## API 配置

前端通过 Vite 代理访问后端 API，代理配置在 `vite.config.js` 中：

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true
  }
}
```

确保后端服务运行在 `http://localhost:8080`

