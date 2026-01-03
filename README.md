# Integrated-marketing-and-service-management-system
近年来，重庆水泵厂有限责任公司（以下简称重泵公司）一直致力于工业化与信息化相结合的发展道路，立足自身实际不断提升信息化水平。为提高客户满意度，适应市场竞争与公司发展新战略，重泵公司着力于打造一套以市场部销售管理、市场部企业协同应用管理、客户服务门户管理、设备运行状态诊断及分析管理、决策分析管理的五大功能模块为主，同时实现与现有信息化系统高效结合，促进销售、运营、生产、服务的一体化管理系统。
# 权限管理平台

基于 Spring Boot + Spring Data JPA + MySQL 的权限管理平台，采用前后端分离架构。

## 技术栈

- **后端**: Spring Boot 3.x, Spring Data JPA, MySQL
- **前端**: 建议使用 Vue 3 或 Angular（未包含在本项目中）

## 功能模块

### 1. 用户注册功能
- 身份证号验证（检查是否已注册）
- 用户注册（姓名、身份证号、手机号、岗位、所属片区）
- 前端解析身份证号获取性别、出生日期、年龄

### 2. 账号管理
- 查看待审核注册信息
- 审核通过/拒绝
- 重置用户密码
- 设置账号角色

### 3. 角色管理
- 角色的增删改查
- 角色名唯一性校验

### 4. 选项管理
- 系统选项的增删改查
- 支持分组管理（岗位、所属片区等）

### 5. 模块管理
- 功能模块的增删改查
- 树形结构展示
- 支持父子模块关系

### 6. 权限配置
- 为角色配置模块权限
- 细粒度权限控制（canRead, canAdd, canUpdate, canSee）
- 树形界面展示已配置权限

## 数据库配置

修改 `src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/rootmanage?useSSL=false&characterEncoding=utf8&serverTimezone=UTC
    username: root
    password: root
```

## 启动步骤

### 1. 配置数据库

1. 创建 MySQL 数据库（数据库名：rootmanage）
2. 修改 `src/main/resources/application.yml` 中的数据库连接信息：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/rootmanage?useSSL=false&characterEncoding=utf8&serverTimezone=UTC
       username: root  # 修改为你的数据库用户名
       password: root  # 修改为你的数据库密码
   ```

### 2. 启动项目

**方法一：使用 Maven 打包后运行（推荐）**

```bash
# 1. 打包项目
mvn clean package -DskipTests

# 2. 运行 JAR 文件
java -jar target/rootmanage-1.0-SNAPSHOT.jar
```

**方法二：使用 IDE 运行**

1. 在 IDE（如 IntelliJ IDEA）中打开项目
2. 找到 `RootManageApplication.java` 文件
3. 右键点击 → Run 'RootManageApplication'

### 3. 验证启动

启动成功后，你会看到类似以下输出：
```
Started RootManageApplication in X.XXX seconds
Tomcat started on port 8080
```

访问地址：`http://localhost:8080`

### 4. 自动建表

项目启动后，JPA 会根据 `application.yml` 中的 `ddl-auto: update` 自动创建数据表。

## API 接口文档

### 认证相关
- `POST /api/auth/login` - 用户登录

### 注册相关
- `GET /api/register/check?idCard=xxx` - 验证身份证号
- `POST /api/register` - 提交注册
- `GET /api/register/pending` - 获取待审核列表
- `PUT /api/register/{id}/approve` - 审核通过
- `PUT /api/register/{id}/reject` - 审核拒绝

### 账号管理
- `GET /api/accounts` - 获取账号列表
- `PUT /api/accounts/{id}/reset-password` - 重置密码
- `PUT /api/accounts/{id}/role` - 修改角色
- `PUT /api/accounts/{id}/status` - 修改状态

### 角色管理
- `GET /api/roles` - 获取角色列表
- `POST /api/roles` - 创建角色
- `PUT /api/roles/{id}` - 更新角色
- `DELETE /api/roles/{id}` - 删除角色

### 选项管理
- `GET /api/options?group=xxx` - 获取选项列表（可按分组过滤）
- `POST /api/options` - 创建选项
- `PUT /api/options/{id}` - 更新选项
- `DELETE /api/options/{id}` - 删除选项

### 模块管理
- `GET /api/modules` - 获取模块列表
- `GET /api/modules/tree` - 获取模块树
- `POST /api/modules` - 创建模块
- `PUT /api/modules/{id}` - 更新模块
- `DELETE /api/modules/{id}` - 删除模块

### 权限配置
- `GET /api/permissions/role/{roleId}` - 获取角色的权限列表
- `GET /api/permissions/role/{roleId}/tree` - 获取带权限状态的模块树
- `POST /api/permissions` - 保存权限配置

## 数据表结构

### 用户账号表 (user_account)
- 主键ID (UUID)
- createTime, updateTime
- 姓名、身份证号、手机号
- 出生日期、性别、年龄
- 岗位（外键选项表）、所属片区（外键选项表）
- 账号名、密码、角色（外键角色表）
- 状态（待审核/已通过/已拒绝）

### 角色表 (role)
- 主键ID (UUID)
- createTime, updateTime
- 角色名、角色描述

### 选项表 (option_item)
- 主键ID (UUID)
- createTime, updateTime
- 所属分组、标题、选项值、选项次序

### 模块表 (module_entity)
- 主键ID (UUID)
- createTime, updateTime
- 中文名称、英文名称、菜单级数、分组
- 父模块ID、链接路径、图标
- 是否父节点、是否展开、是否可见

### 权限配置表 (role_module_permission)
- 主键ID (UUID)
- createTime, updateTime
- 角色ID、模块ID
- canRead、canAdd、canUpdate、canSee

## 注意事项

1. 默认密码：管理员审核通过后，账号名默认为手机号，密码默认为 `123456`
2. 密码加密：使用 BCrypt 加密存储
3. CORS 配置：已配置允许跨域访问，生产环境应限制具体的前端地址
4. 身份证解析：由前端完成，后端接收解析后的数据

## 开发说明

- 项目采用 RESTful API 设计
- 统一异常处理：`RestExceptionHandler`
- 统一响应格式：直接返回实体或列表
- 数据验证：使用 Jakarta Validation

## 前端开发建议

1. 使用 Vue 3 + Element Plus 或 Angular + Material
2. 实现动态路由和菜单（根据用户权限）
3. 实现权限指令/组件控制按钮显隐
4. 身份证号解析可使用第三方库或自行实现

