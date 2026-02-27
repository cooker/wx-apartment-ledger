## 领域设计与技术方案

> 本文档用于说明房屋租赁管理系统的领域模型、数据表结构与 DDD 分层设计。  
> 若后续对领域模型或表结构有较大调整，请同步更新本文件与 `docs/sql/` 下的 SQL 脚本。

### 1. 领域概览

- **租客（Tenant）**
  - 属性：姓名、手机号、微信号、备注等
  - 行为：查看账单、支付账单
- **房屋（House）**
  - 属性：房屋编号、地址/名称、月租金、押金、入住日期、当前租客、绑定的水表与电表
  - 行为：计算某一账期内的应收租金、水费、电费
- **公共场所（SharedPlace）**
  - 属性：名称、绑定的水表与电表、关联租客（用于分摊）
  - 特性：水电分摊部分需要人工调整
- **计量表（Meter）**
  - 类型：电表、水表
  - 属性：表编号、表类型（电/水）、安装位置、是否为总表
  - 行为：按月记录用量、与总表进行核对
- **计量表集合（MeterSet）**
  - 用途：支持一个总表关联多个子表，用于核对总表与分表用量差异
- **计量表月度用量（MeterMonthlyUsage）**
  - 属性：计量表、账期（年-月）、上月读数、本月读数、本月用量
- **价格配置（PriceConfig）**
  - 属性：费用类型（水/电）、单位价格、生效时间等
- **账单（Bill）**
  - 维度：按租客 + 账期（年-月）
  - 构成：房屋租金 + 水费 + 电费（含公共场所分摊调整）
  - 状态：未结清 / 已结清
  - 特性：
    - 未结清账单允许重新生成
    - 已结清账单禁止重新生成
    - 支持按“租客维度”录入收款金额，或默认一次性收全额

### 2. DDD 分层设计

后端采用典型的分层结构：

- `domain`（领域层）
  - 领域实体：`Tenant`, `House`, `SharedPlace`, `Meter`, `MeterSet`, `Bill` 等
  - 值对象：如 `Money`, `YearMonth`, `UsageAmount` 等（如有需要）
  - 领域服务：账单生成规则、公共区域水电分摊、账单重新生成校验逻辑等
- `application.*`（应用层）
  - 应用服务：面向用例（如“生成某月账单”、“结清账单”、“更新租客信息”）
  - 负责：事务边界控制、调用领域服务与仓储、DTO 转换
- `infrastructure`（基础设施层）
  - MyBatis-Plus 实体（PO）、Mapper、Repository 实现
  - 对外资源适配（数据库、消息队列等）
- `interfaces`（接口层）
  - REST Controller：暴露 `/api/...` 接口，仅做编排与参数校验，不编写业务规则

### 3. 数据库表设计（初稿）

> 所有表名和字段名 **禁止使用 MySQL 保留关键字**，例如：`user`、`order`、`group`、`key`、`desc` 等。  
> 金额类型统一使用 `DECIMAL(14, 2)`，Java 中使用 `BigDecimal`。

#### 3.1 租客表：`t_tenant`

- `id`：主键，自增或雪花 ID
- `full_name`：姓名
- `mobile_number`：手机号
- `wechat_id`：微信号
- `remark_text`：备注
- `created_at`：创建时间
- `updated_at`：更新时间

#### 3.2 房屋表：`t_house`

- `id`：主键
- `house_code`：房屋编号（例如：A-101）
- `house_name`：显示名称（可选）
- `location_text`：地址（可选）
- `rent_amount`：月租金，`DECIMAL(14, 2)`
- `deposit_amount`：押金，`DECIMAL(14, 2)`
- `check_in_date`：入住日期
- `current_tenant_id`：当前租客 ID（外键指向 `t_tenant.id`）
- `electric_meter_id`：绑定电表 ID（外键指向 `t_meter.id`）
- `water_meter_id`：绑定水表 ID（外键指向 `t_meter.id`）
- `created_at`：创建时间
- `updated_at`：更新时间

#### 3.3 公共场所表：`t_shared_place`

- `id`：主键
- `place_name`：名称（如“楼道公共区”）
- `electric_meter_id`：绑定电表 ID
- `water_meter_id`：绑定水表 ID
- `related_tenant_id`：关联租客 ID（用于后续公摊）
- `created_at`：创建时间
- `updated_at`：更新时间

#### 3.4 计量表表：`t_meter`

- `id`：主键
- `meter_code`：表编号
- `meter_kind`：表类型，枚举值（`ELECTRIC` / `WATER`）
- `install_location`：安装位置描述
- `is_main_meter`：是否为总表（布尔）
- `created_at`：创建时间
- `updated_at`：更新时间

#### 3.5 计量表集合表：`t_meter_set` 与关联表 `t_meter_set_item`

- `t_meter_set`
  - `id`：主键
  - `set_name`：集合名称（例如“1 楼电表分表集合”）
  - `main_meter_id`：总表 ID（指向 `t_meter.id`）
  - `created_at`：创建时间
  - `updated_at`：更新时间
- `t_meter_set_item`
  - `id`：主键
  - `set_id`：集合 ID（指向 `t_meter_set.id`）
  - `meter_id`：子表 ID（指向 `t_meter.id`）

#### 3.6 计量表月度用量表：`t_meter_monthly_usage`

- `id`：主键
- `meter_id`：计量表 ID（指向 `t_meter.id`）
- `usage_year`：年份（如 2025）
- `usage_month`：月份（1-12）
- `previous_reading`：上月读数
- `current_reading`：本月读数
- `usage_value`：本月用量（可冗余保存，便于查询）
- `created_at`：创建时间
- `updated_at`：更新时间

#### 3.7 价格配置表：`t_price_config`

- `id`：主键
- `charge_kind`：费用类型（`ELECTRIC` / `WATER`）
- `unit_price`：单价，`DECIMAL(14, 4)`（保留更多精度）
- `effective_date`：生效日期
- `expired_date`：失效日期（可为空）
- `created_at`：创建时间
- `updated_at`：更新时间

#### 3.8 账单主表：`t_bill`

- `id`：主键
- `tenant_id`：租客 ID
- `bill_year`：账单年份
- `bill_month`：账单月份
- `house_id`：房屋 ID（普通房屋账单场景）
- `shared_place_id`：公共场所 ID（如有专门公共场所对应租客）
- `rent_amount`：本期房屋租金
- `water_amount`：本期水费（含公共区域分摊）
- `electric_amount`：本期电费（含公共区域分摊）
- `adjust_amount`：手工调整金额（正数代表加收，负数代表减免）
- `total_amount`：应收总金额
- `settle_state`：结清状态（例如：`PENDING` / `SETTLED`）
- `paid_amount`：已收金额
- `paid_time`：最近一次收款时间
- `created_at`：创建时间
- `updated_at`：更新时间

> 规则：  
> - 未结清账单允许重新生成（会根据当前规则重新计算相关金额）。  
> - 已结清账单禁止重新生成（需要明确的业务操作来撤销或冲正）。  

#### 3.9 账单收款记录表：`t_bill_payment`

- `id`：主键
- `bill_id`：账单 ID
- `payment_amount`：本次收款金额
- `payment_time`：收款时间
- `payment_note`：备注（如“微信转账”、“现金”）
- `created_at`：创建时间

### 4. 账单生成与重新生成规则（概要）

1. **账单生成入口**
   - 以“账期（年-月）+ 租客”为维度，遍历所有房屋与公共场所关联关系。
2. **房屋账单计算**
   - 按房屋记录中的 `rent_amount` 计入租金。
   - 查找对应水表和电表在该账期的 `t_meter_monthly_usage`，结合 `t_price_config` 计算水费、电费。
3. **公共场所账单计算**
   - 公共场所对应的用量通过水表/电表用量获取。
   - 分摊逻辑与调整部分可由领域服务处理，结果写入 `water_amount` / `electric_amount` 与 `adjust_amount`。
4. **重新生成规则**
   - 若账单 `settle_state` 为未结清，则允许重新生成：
     - 根据当前最新的价格配置、用量数据重新计算账单金额。
   - 若账单 `settle_state` 为已结清，则拒绝重新生成，返回明确错误信息。

### 5. REST API 设计约定

- 所有接口路径以 `/api/` 为前缀。
- 仅使用 **GET/POST**，示例：
  - 查询类：`GET /api/bills?tenantId=...&year=...&month=...`
  - 新建租客：`POST /api/tenants/create`
  - 更新房屋信息：`POST /api/houses/{id}/update`
  - 结清账单：`POST /api/bills/{id}/settle`

### 6. 后续工作指引

- 在实现具体功能模块时：
  - 补充/调整 `docs/sql/*.sql` 中的表结构与字段。
  - 对涉及金额计算、账单生成/结清的逻辑，优先编写单元测试。
  - 如有对领域模型或聚合边界的调整，需同步更新本 `DESIGN.md`。

