## 前端项目说明（Vue 3 + Vite 5）

本目录为房屋租赁管理系统的前端项目，使用 **Vue 3 + Vite 5**，UI 框架规划为 `art-design-pro`，用于构建管理后台界面。

### 目录结构规划

- `src/`
  - `views/`：按业务领域拆分的页面视图（如：租客管理、房屋管理、账单管理等）
  - `components/`：通用组件与领域组件
  - `api/`：统一封装后端接口调用（不在组件内直接写 `fetch` / `axios`）
  - `styles/`：
    - `variables.css`：主题变量
    - `base.css`：基础重置
    - `common.css`：通用 UI 组件样式
    - `index.css`：样式聚合入口
  - `router/`（可选）：前端路由配置
  - `store/`（可选）：状态管理（如 Pinia）

### 开发命令

在 `frontend/` 目录下执行：

- 安装依赖（推荐）：
  - `pnpm install`
  - 如无 pnpm，可使用 `npm install`
- 启动开发环境：
  - `pnpm dev` 或 `npm run dev`
- 打包构建：
  - `pnpm build` 或 `npm run build`

### 与后端联调约定

- 所有后端接口统一以 `/api/` 为前缀，仅使用 **GET/POST**：
  - 查询：GET `/api/...`
  - 新增/更新：POST `/api/...`
- 前端通过 `src/api/*.ts` 统一封装请求函数，例如：
  - `getTenantList(params)`
  - `createOrUpdateTenant(payload)`
  - `generateMonthlyBill(payload)`

### UI 与交互风格

- 采用 `art-design-pro` 构建整体布局和基础组件（导航、表格、表单等）。
- 页面遵循以下原则：
  - 列表 + 条件筛选 + 分页
  - 详情与编辑表单可使用 Drawer / Modal
  - 对金额、日期等关键字段保持清晰的展示格式

