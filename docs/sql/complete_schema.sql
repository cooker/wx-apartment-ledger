-- =============================================================================
-- 房屋租赁管理系统 - 完整建表脚本
-- =============================================================================
-- 约定：1) 表名/字段禁止 MySQL 保留字  2) 金额用 DECIMAL(14,2)  3) 禁止外键
-- 用途：新库初始化执行本脚本即可；旧库迁移见文末「可选迁移」节
-- =============================================================================

-- ----- 租客 -----
CREATE TABLE IF NOT EXISTS t_tenant (
    id              BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    full_name       VARCHAR(64)    NOT NULL COMMENT '姓名',
    mobile_number   VARCHAR(32)    NOT NULL COMMENT '手机号',
    wechat_id       VARCHAR(64)    NULL COMMENT '微信号',
    remark_text     VARCHAR(255)   NULL COMMENT '备注',
    created_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='租客信息';

-- ----- 水电计量表 -----
CREATE TABLE IF NOT EXISTS t_meter (
    id               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    meter_code       VARCHAR(64)  NOT NULL COMMENT '计量表编号',
    meter_kind       VARCHAR(16)  NOT NULL COMMENT '计量表类型 ELECTRIC/WATER',
    install_location VARCHAR(128) NULL COMMENT '安装位置',
    is_main_meter    TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否总表 1=是,0=否',
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='水电计量表';

CREATE TABLE IF NOT EXISTS t_meter_set (
    id            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    set_name      VARCHAR(64)  NOT NULL COMMENT '计量表集合名称',
    main_meter_id BIGINT UNSIGNED NOT NULL COMMENT '主表ID t_meter.id',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_meter_set_name UNIQUE KEY (set_name)
) COMMENT='计量表集合';

CREATE TABLE IF NOT EXISTS t_meter_set_item (
    id        BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    set_id    BIGINT UNSIGNED NOT NULL COMMENT '集合ID t_meter_set.id',
    meter_id  BIGINT UNSIGNED NOT NULL COMMENT '子表ID t_meter.id',
    created_at DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='计量表集合成员';

CREATE TABLE IF NOT EXISTS t_meter_monthly_usage (
    id               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    meter_id         BIGINT UNSIGNED NOT NULL COMMENT '计量表ID t_meter.id',
    usage_year       INT            NOT NULL COMMENT '年份',
    usage_month      TINYINT        NOT NULL COMMENT '月份 1-12',
    previous_reading DECIMAL(18,4)  NOT NULL COMMENT '上月读数',
    current_reading  DECIMAL(18,4)  NOT NULL COMMENT '本月读数',
    usage_value      DECIMAL(18,4)  NOT NULL COMMENT '本月用量',
    created_at       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_meter_monthly UNIQUE KEY (meter_id, usage_year, usage_month)
) COMMENT='计量表月度用量';

-- ----- 房屋 -----
CREATE TABLE IF NOT EXISTS t_house (
    id                BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    house_code        VARCHAR(64)   NOT NULL COMMENT '房屋编号',
    house_name        VARCHAR(128)  NULL COMMENT '房屋名称',
    location_text     VARCHAR(255)  NULL COMMENT '地址描述',
    rent_amount       DECIMAL(14,2) NOT NULL COMMENT '月租金',
    deposit_amount    DECIMAL(14,2) NOT NULL COMMENT '押金',
    check_in_date     DATE          NULL COMMENT '入住日期',
    current_tenant_id BIGINT UNSIGNED NULL COMMENT '当前租客ID t_tenant.id',
    electric_meter_id BIGINT UNSIGNED NULL COMMENT '绑定电表ID t_meter.id',
    water_meter_id    BIGINT UNSIGNED NULL COMMENT '绑定水表ID t_meter.id',
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_house_code UNIQUE KEY (house_code)
) COMMENT='房屋信息';

CREATE TABLE IF NOT EXISTS t_house_meter (
    id        BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    house_id  BIGINT UNSIGNED NOT NULL COMMENT '房屋ID t_house.id',
    meter_id  BIGINT UNSIGNED NOT NULL COMMENT '计量表ID t_meter.id',
    created_at DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='房屋-计量表关联';

-- ----- 公共场所 -----
CREATE TABLE IF NOT EXISTS t_shared_place (
    id                BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    place_name        VARCHAR(128)  NOT NULL COMMENT '公共场所名称',
    electric_meter_id BIGINT UNSIGNED NULL COMMENT '绑定电表ID t_meter.id',
    water_meter_id    BIGINT UNSIGNED NULL COMMENT '绑定水表ID t_meter.id',
    related_tenant_id BIGINT UNSIGNED NULL COMMENT '关联租客ID t_tenant.id',
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_shared_place_name UNIQUE KEY (place_name)
) COMMENT='公共场所信息';

CREATE TABLE IF NOT EXISTS t_shared_place_meter (
    id              BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    shared_place_id BIGINT UNSIGNED NOT NULL COMMENT '公共场所ID t_shared_place.id',
    meter_id        BIGINT UNSIGNED NOT NULL COMMENT '计量表ID t_meter.id',
    created_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='公共场所-计量表关联';

CREATE TABLE IF NOT EXISTS t_shared_place_tenant (
    id              BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    shared_place_id BIGINT UNSIGNED NOT NULL COMMENT '公共场所ID t_shared_place.id',
    tenant_id       BIGINT UNSIGNED NOT NULL COMMENT '租客ID t_tenant.id',
    created_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='公共场所-租客关联';

CREATE TABLE IF NOT EXISTS t_shared_place_tenant_share (
    id               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    shared_place_id  BIGINT UNSIGNED NOT NULL COMMENT '公共场所ID t_shared_place.id',
    tenant_id        BIGINT UNSIGNED NOT NULL COMMENT '租客ID t_tenant.id',
    usage_year       INT             NOT NULL COMMENT '年份',
    usage_month      TINYINT         NOT NULL COMMENT '月份 1-12',
    electric_usage   DECIMAL(18,4)   NOT NULL DEFAULT 0 COMMENT '本月该租客分摊的电量',
    water_usage      DECIMAL(18,4)   NOT NULL DEFAULT 0 COMMENT '本月该租客分摊的水量',
    electric_amount  DECIMAL(14,2)   NOT NULL DEFAULT 0 COMMENT '本月该租客分摊的电费',
    water_amount     DECIMAL(14,2)   NOT NULL DEFAULT 0 COMMENT '本月该租客分摊的水费',
    created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_shared_place_tenant_share UNIQUE KEY (shared_place_id, tenant_id, usage_year, usage_month)
) COMMENT='公共场所-租客水电分摊（按公共场所+租客+年月唯一）';

-- ----- 水电单价 -----
CREATE TABLE IF NOT EXISTS t_price_config (
    id            BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    charge_kind   VARCHAR(16)   NOT NULL COMMENT '费用类型 ELECTRIC/WATER',
    unit_price    DECIMAL(14,4) NOT NULL COMMENT '单价',
    effective_date DATE         NOT NULL COMMENT '生效日期',
    expired_date   DATE         NULL COMMENT '失效日期',
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='水电单价配置';

-- ----- 账单 -----
CREATE TABLE IF NOT EXISTS t_bill (
    id              BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    tenant_id       BIGINT UNSIGNED NOT NULL COMMENT '租客ID t_tenant.id',
    bill_year       INT             NOT NULL COMMENT '账单年份',
    bill_month      TINYINT         NOT NULL COMMENT '账单月份 1-12',
    rent_amount     DECIMAL(14,2)   NOT NULL DEFAULT 0 COMMENT '租金金额',
    water_amount    DECIMAL(14,2)   NOT NULL DEFAULT 0 COMMENT '水费金额（含公共区域分摊）',
    electric_amount DECIMAL(14,2)   NOT NULL DEFAULT 0 COMMENT '电费金额（含公共区域分摊）',
    adjust_amount   DECIMAL(14,2)   NOT NULL DEFAULT 0 COMMENT '手工调整金额',
    total_amount    DECIMAL(14,2)   NOT NULL COMMENT '应收总金额',
    settle_state    VARCHAR(16)     NOT NULL DEFAULT 'PENDING' COMMENT '结清状态 PENDING/SETTLED',
    paid_amount     DECIMAL(14,2)   NOT NULL DEFAULT 0 COMMENT '已收金额',
    paid_time       DATETIME        NULL COMMENT '最近收款时间',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_bill_tenant_month UNIQUE KEY (tenant_id, bill_year, bill_month)
) COMMENT='租客月度账单（按租客+年月唯一，一条记录通过关联表覆盖多套房屋和多个公共场所）';

CREATE TABLE IF NOT EXISTS t_bill_house (
    id               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    bill_id          BIGINT UNSIGNED NOT NULL COMMENT '账单ID t_bill.id',
    house_id         BIGINT UNSIGNED NOT NULL COMMENT '房屋ID t_house.id',
    share_ratio      DECIMAL(6,4)    NULL COMMENT '该房屋在本账单中的分摊比例，可为空表示均分',
    electric_usage   DECIMAL(18,4)   NOT NULL DEFAULT 0 COMMENT '本账单中该房屋对应的电量',
    water_usage      DECIMAL(18,4)   NOT NULL DEFAULT 0 COMMENT '本账单中该房屋对应的水量',
    electric_amount  DECIMAL(14,2)   NOT NULL DEFAULT 0 COMMENT '本账单中该房屋对应的电费',
    water_amount     DECIMAL(14,2)   NOT NULL DEFAULT 0 COMMENT '本账单中该房屋对应的水费',
    created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='账单-房屋关联：一个租客月度账单可关联多套房屋（含分摊比例、水电用量与费用）';

CREATE TABLE IF NOT EXISTS t_bill_shared_place (
    id               BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    bill_id          BIGINT UNSIGNED NOT NULL COMMENT '账单ID t_bill.id',
    shared_place_id  BIGINT UNSIGNED NOT NULL COMMENT '公共场所ID t_shared_place.id',
    share_ratio      DECIMAL(6,4)    NULL COMMENT '该公共场所在本账单中的分摊比例，可为空表示均分',
    electric_usage   DECIMAL(18,4)   NOT NULL DEFAULT 0 COMMENT '本账单中该公共场所对应的电量',
    water_usage      DECIMAL(18,4)   NOT NULL DEFAULT 0 COMMENT '本账单中该公共场所对应的水量',
    electric_amount  DECIMAL(14,2)   NOT NULL DEFAULT 0 COMMENT '本账单中该公共场所对应的电费',
    water_amount     DECIMAL(14,2)   NOT NULL DEFAULT 0 COMMENT '本账单中该公共场所对应的水费',
    created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='账单-公共场所关联：一个租客月度账单可关联多个公共场所（含分摊比例、水电用量与费用）';

CREATE TABLE IF NOT EXISTS t_bill_payment (
    id             BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    bill_id        BIGINT UNSIGNED NOT NULL COMMENT '账单ID t_bill.id',
    payment_amount DECIMAL(14,2)   NOT NULL COMMENT '本次收款金额',
    payment_time   DATETIME        NOT NULL COMMENT '收款时间',
    payment_note   VARCHAR(255)    NULL COMMENT '收款备注',
    created_at     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='账单收款记录';

-- =============================================================================
-- 可选：从旧版 t_bill（含 house_id、shared_place_id 单列）迁移到多关联表
-- 仅当 t_bill 仍含上述列时执行；执行前请备份。新库请勿执行本节。
-- =============================================================================
/*
-- 1. 迁移房屋关联
INSERT INTO t_bill_house (bill_id, house_id, created_at)
SELECT id, house_id, created_at FROM t_bill WHERE house_id IS NOT NULL;

-- 2. 迁移公共场所关联
INSERT INTO t_bill_shared_place (bill_id, shared_place_id, created_at)
SELECT id, shared_place_id, created_at FROM t_bill WHERE shared_place_id IS NOT NULL;

-- 3. 删除旧列
ALTER TABLE t_bill DROP COLUMN house_id, DROP COLUMN shared_place_id;
*/
