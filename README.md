## 房屋租赁管理系统

本项目用于管理房屋/公共场所的水电、租金及账单信息，支持按月生成租客账单，适合小型到中型房屋租赁场景。

### 技术栈

- **后端**：Java 21、Spring Boot 3.2、MyBatis-Plus、MySQL 8，DDD 分层（`domain` / `application.*` / `infrastructure` / `interfaces`）
- **前端**：Vue 3 + Vite 5，UI 框架使用 `art-design-pro`
- **构建工具**：Maven（后端）、pnpm（前端，推荐）/ npm

### 仓库结构（规划）

- `backend/`：后端 Spring Boot 项目
  - `src/main/java/.../domain/`：领域层（实体、值对象、领域服务）
  - `src/main/java/.../application/`：应用层（用例编排、DTO 转换、事务）
  - `src/main/java/.../infrastructure/`：基础设施层（MyBatis-Plus、Repository 实现）
  - `src/main/java/.../interfaces/`：接口层（REST Controller，仅编排）
  - `src/main/resources/`：配置文件（`application.yml` 等）
- `frontend/`：前端 Vue 3 + Vite 项目
  - `src/views/`：按业务领域拆分的页面
  - `src/components/`：通用组件与领域组件
  - `src/api/`：统一封装后端 API 调用
  - `src/styles/`：全局样式与主题
- `docs/`
  - `DESIGN.md`：领域模型、表结构与 DDD 分层说明
  - `sql/`：数据库建表与迁移脚本（如 `V1__init_schema.sql`）
- `TODO.md`：AI 协作使用说明（系统提示词）

### 业务范围概览

- **水电管理**：配置水费、电费单价
- **电表管理**：按月维护用电量（本月/上月），支持电表集合用于与总表核对
- **水表管理**：按月维护用水量（本月/上月），支持水表集合用于与总表核对
- **房屋管理**：维护房屋绑定的电表、水表、租金、押金、入住日期、租客
- **公共场所管理**：维护公共区域绑定的电表、水表、租客（用于分摊）
- **租客管理**：姓名、手机号、微信号
- **账单管理**：
  - 按年月拉出租客关联的账单信息（房屋、水、电）
  - 公共场所水、电需要人工调整
  - 未结清账单允许重新生成；已结清账单禁止重新生成
  - 支持租客维度录入收款金额，或默认全部金额一次性收款

### REST 接口约定

- 仅使用 **GET/POST**：
  - 查询类：GET `/api/...`
  - 新增/更新类：POST `/api/...`，更新操作采用语义化后缀，例如：
    - `POST /api/bills/{id}/update`
    - `POST /api/bills/{id}/settle`
- 不使用 PUT/PATCH/DELETE 等方法。

### 本地开发环境准备

- **必须组件**：
  - JDK 21+
  - Maven 3.9+
  - Node.js 18+、pnpm（推荐）或 npm
  - MySQL 8.x

### 后端启动步骤

1. 进入后端目录：
   - `cd backend`
2. 导入 Maven 项目（IDEA / VS Code），或直接执行：
   - `mvn clean package`
3. 启动应用：
   - `mvn spring-boot:run`
4. 默认本地端口（暂定）：`http://localhost:8080`

> 数据库连接信息在 `backend/src/main/resources/application.yml` 中配置，请根据本地环境修改。

### 前端启动步骤

1. 进入前端目录：
   - `cd frontend`
2. 安装依赖（推荐）：
   - `pnpm install`
   - 若未安装 pnpm，可使用 `npm install`
3. 启动开发服务器：
   - `pnpm dev` 或 `npm run dev`
4. 访问前端地址（Vite 默认）：
   - `http://localhost:5173`

### 数据库与建表 SQL 说明

- 所有建表脚本统一放在 `docs/sql/` 目录下，例如：
  - `docs/sql/V1__init_schema.sql`
- **重要约定**：  
  - 表名和字段名 **禁止使用任何 MySQL 保留关键字**（例如：`user`、`order`、`group`、`key`、`desc` 等），以避免后续迁移或兼容性问题。
  - 金额类字段统一使用 `DECIMAL(14, 2)` 类型，在 Java 中对应 `BigDecimal`。

### 后续规划

- 在 `docs/DESIGN.md` 中进一步细化：
  - 领域模型与聚合边界（租客、房屋、水电表、账单等）
  - 数据表结构与字段设计
  - 账单生成、结清、重新生成的业务规则
- 后端按模块逐步补充：
  - 租客管理模块
  - 房屋与公共场所管理模块
  - 水电表与用量记录模块
  - 账单生成与结算模块
- 前端按领域视图拆分页面，使用 `art-design-pro` 构建统一的管理后台 UI 体验。

