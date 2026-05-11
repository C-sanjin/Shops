# 多门店O2O商城系统 - 设计文档

## 1. 项目概述

### 1.1 项目名称
ShopX - 多门店O2O商城系统

### 1.2 项目目标
开发一套支持多门店管理、虚拟支付、核销码生成/核销的H5商城系统，支持门店代付和自付+指定门店核销两种模式。

### 1.3 核心特性
- 多门店管理（O2O模式）
- 虚拟支付功能（后台管理指定门店使用）
- 核销码生成与扫码核销
- 门店代付模式
- 自付+指定门店核销模式
- 微信支付/支付宝支付（后续迭代）

### 1.4 设计原则
- 安全性：支付安全、核销防重、数据加密
- 稳定性：分布式锁、乐观锁、事务保障
- 可维护性：模块化分层、代码规范
- 易用性：简洁大方的H5界面

## 2. 技术栈

| 层级 | 技术选型 | 版本 |
|------|---------|------|
| 前端H5 | Vue3 + Vant4 + Pinia + Vue Router | Vue3.4+ |
| 后端 | Spring Boot + MyBatis-Plus | Spring Boot 3.x |
| 数据库 | MySQL + Redis | MySQL 8.x / Redis 7.x |
| 认证 | JWT + Spring Security | - |
| 接口文档 | Knife4j (Swagger) | - |
| 构建工具 | Maven | 3.9+ |
| JDK | OpenJDK | 17+ |

## 3. 系统架构

### 3.1 架构模式
单体应用 + 模块化分层。内部按业务域划分为独立模块，模块间通过Service接口通信，后续可拆分为微服务。

### 3.2 后端项目结构

```
shop-server/
├── shop-common/          # 公共模块（工具类、常量、异常、基础Entity）
├── shop-model/           # 数据模型（Entity、DTO、VO、枚举）
├── shop-dao/             # 数据访问层（Mapper接口、Redis操作类）
├── shop-service/         # 业务逻辑层
│   ├── user/             # 用户模块（注册、登录、个人信息）
│   ├── store/            # 门店模块（门店CRUD、额度管理）
│   ├── product/          # 商品模块（商品CRUD、库存管理）
│   ├── order/            # 订单模块（下单、取消、过期处理）
│   ├── payment/          # 支付模块（虚拟支付、微信/支付宝、代付）
│   └── verification/     # 核销模块（核销码生成、核销、批量操作）
├── shop-admin/           # 后台管理API（Controller层）
├── shop-api/             # H5用户端API（Controller层）
└── shop-job/             # 定时任务（订单过期、核销码过期清理）
```

### 3.3 前端项目结构

```
shop-h5/
├── src/
│   ├── views/
│   │   ├── home/         # 首页（商品推荐、门店入口）
│   │   ├── product/      # 商品列表/详情
│   │   ├── order/        # 下单/订单列表/订单详情
│   │   ├── payment/      # 支付页（选择支付方式、选择门店）
│   │   ├── verification/ # 核销码展示（二维码+数字码）
│   │   ├── store/        # 门店列表/详情/核销入口
│   │   └── user/         # 个人中心
│   ├── stores/           # Pinia状态管理
│   ├── api/              # 接口请求封装
│   ├── utils/            # 工具函数
│   ├── router/           # 路由配置
│   └── components/       # 公共组件
```

## 4. 数据库设计

### 4.1 门店表 t_store

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | PK | 主键 |
| store_name | VARCHAR(100) | Y | 门店名称 |
| store_code | VARCHAR(32) | Y | 门店编码（唯一索引） |
| address | VARCHAR(255) | Y | 门店地址 |
| contact_phone | VARCHAR(20) | N | 联系电话 |
| business_hours | VARCHAR(50) | N | 营业时间 |
| status | TINYINT | Y | 状态 0禁用 1启用 |
| longitude | DECIMAL(10,6) | N | 经度 |
| latitude | DECIMAL(10,6) | N | 纬度 |
| created_at | DATETIME | Y | 创建时间 |
| updated_at | DATETIME | Y | 更新时间 |
| deleted | TINYINT | Y | 逻辑删除 0否 1是 |

### 4.2 商品表 t_product

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | PK | 主键 |
| product_name | VARCHAR(200) | Y | 商品名称 |
| price | DECIMAL(10,2) | Y | 售价 |
| original_price | DECIMAL(10,2) | N | 原价 |
| stock | INT | Y | 库存 |
| description | TEXT | N | 商品详情 |
| images | JSON | N | 图片列表 |
| store_scope | TINYINT | Y | 适用范围 0全部门店 1指定门店 |
| status | TINYINT | Y | 状态 0下架 1上架 |
| created_at | DATETIME | Y | 创建时间 |
| updated_at | DATETIME | Y | 更新时间 |
| deleted | TINYINT | Y | 逻辑删除 |

### 4.3 商品-门店关联表 t_product_store

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | PK | 主键 |
| product_id | BIGINT | Y | 商品ID（索引） |
| store_id | BIGINT | Y | 门店ID（索引） |

联合唯一索引：(product_id, store_id)

### 4.4 用户表 t_user

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | PK | 主键 |
| phone | VARCHAR(20) | Y | 手机号（唯一索引） |
| password | VARCHAR(100) | Y | 密码（BCrypt加密） |
| nickname | VARCHAR(50) | N | 昵称 |
| avatar | VARCHAR(255) | N | 头像URL |
| status | TINYINT | Y | 状态 0禁用 1启用 |
| created_at | DATETIME | Y | 创建时间 |
| updated_at | DATETIME | Y | 更新时间 |

### 4.5 订单表 t_order

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | PK | 主键 |
| order_no | VARCHAR(32) | Y | 订单号（唯一索引） |
| user_id | BIGINT | Y | 用户ID（索引） |
| store_id | BIGINT | Y | 核销门店ID |
| total_amount | DECIMAL(10,2) | Y | 订单金额 |
| pay_amount | DECIMAL(10,2) | Y | 实付金额 |
| pay_type | TINYINT | Y | 支付方式 1微信 2支付宝 3虚拟支付 4门店代付 |
| pay_status | TINYINT | Y | 支付状态 0未付 1已付 2已退款 |
| order_status | TINYINT | Y | 订单状态 0待付 1待核销 2已核销 3已取消 4已过期 |
| proxy_store_id | BIGINT | N | 代付门店ID（仅代付模式时填写，此时store_id=proxy_store_id，即代付门店同时也是核销门店） |
| remark | VARCHAR(500) | N | 备注 |
| expire_time | DATETIME | N | 核销过期时间 |
| pay_time | DATETIME | N | 支付时间 |
| created_at | DATETIME | Y | 创建时间 |
| updated_at | DATETIME | Y | 更新时间 |

索引：(user_id, order_status), (store_id, order_status)

### 4.6 订单明细表 t_order_item

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | PK | 主键 |
| order_id | BIGINT | Y | 订单ID（索引） |
| product_id | BIGINT | Y | 商品ID |
| product_name | VARCHAR(200) | Y | 商品名称（快照） |
| price | DECIMAL(10,2) | Y | 单价（快照） |
| quantity | INT | Y | 数量 |

### 4.7 核销码表 t_verification_code（核心表）

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | PK | 主键 |
| code | VARCHAR(20) | Y | 核销码（唯一索引） |
| order_id | BIGINT | Y | 关联订单ID（索引） |
| order_no | VARCHAR(32) | Y | 关联订单号 |
| user_id | BIGINT | Y | 用户ID（索引） |
| store_id | BIGINT | Y | 指定核销门店ID（索引） |
| status | TINYINT | Y | 状态 0未使用 1已使用 2已过期 3已作废 |
| used_time | DATETIME | N | 核销时间 |
| verified_by | BIGINT | N | 核销操作人ID |
| expire_time | DATETIME | Y | 过期时间 |
| batch_no | VARCHAR(32) | N | 批次号（批量生成时） |
| version | INT | Y | 乐观锁版本号 |
| created_at | DATETIME | Y | 创建时间 |
| updated_at | DATETIME | Y | 更新时间 |

索引：(store_id, status), (batch_no), (expire_time)

### 4.8 虚拟支付配置表 t_virtual_payment_config

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | PK | 主键 |
| config_name | VARCHAR(100) | Y | 配置名称 |
| total_amount | DECIMAL(10,2) | Y | 总金额 |
| used_amount | DECIMAL(10,2) | Y | 已用金额 |
| store_id | BIGINT | Y | 指定门店ID（0=全部门店可用，非0=仅该门店可用。当为0时用户下单可任选门店核销；当为具体门店时，用户只能在该门店核销） |
| status | TINYINT | Y | 状态 0禁用 1启用 |
| expire_time | DATETIME | N | 过期时间 |
| version | INT | Y | 乐观锁版本号 |
| created_at | DATETIME | Y | 创建时间 |
| updated_at | DATETIME | Y | 更新时间 |

### 4.9 门店代付额度表 t_store_payment_quota

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | PK | 主键 |
| store_id | BIGINT | Y | 门店ID（唯一索引） |
| total_quota | DECIMAL(10,2) | Y | 总额度 |
| used_quota | DECIMAL(10,2) | Y | 已用额度 |
| status | TINYINT | Y | 状态 0禁用 1启用 |
| version | INT | Y | 乐观锁版本号 |
| created_at | DATETIME | Y | 创建时间 |
| updated_at | DATETIME | Y | 更新时间 |

### 4.10 管理员表 t_admin

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | PK | 主键 |
| username | VARCHAR(50) | Y | 用户名（唯一索引） |
| password | VARCHAR(100) | Y | 密码（BCrypt加密） |
| real_name | VARCHAR(50) | N | 真实姓名 |
| phone | VARCHAR(20) | N | 手机号 |
| role | TINYINT | Y | 角色 1超级管理员 2门店管理员 |
| store_id | BIGINT | N | 关联门店ID（门店管理员时） |
| status | TINYINT | Y | 状态 0禁用 1启用 |
| created_at | DATETIME | Y | 创建时间 |
| updated_at | DATETIME | Y | 更新时间 |

### 4.11 操作日志表 t_operation_log

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | PK | 主键 |
| operator_id | BIGINT | Y | 操作人ID |
| operator_type | TINYINT | Y | 操作人类型 1管理员 2用户 |
| module | VARCHAR(50) | Y | 模块名 |
| action | VARCHAR(50) | Y | 操作名 |
| detail | TEXT | N | 操作详情（JSON） |
| ip | VARCHAR(50) | N | IP地址 |
| created_at | DATETIME | Y | 创建时间 |

### 4.12 Redis数据结构

| Key模式 | 类型 | 用途 | TTL |
|---------|------|------|-----|
| verify:lock:{code} | String | 核销分布式锁 | 30秒 |
| verify:code:{code} | Hash | 核销码缓存 | 与过期时间一致 |
| order:expire:{orderId} | String | 订单过期延迟标记 | 与过期时间一致 |
| store:quota:{storeId} | Hash | 门店代付额度缓存 | 1小时 |
| rate:limit:{api}:{ip} | String | 接口限流计数 | 1分钟 |

## 5. 核心业务流程

### 5.1 虚拟支付 + 核销码生成流程

```
1. 用户选择商品 → 选择核销门店 → 提交订单
2. 系统创建订单（状态：待付，pay_type=3虚拟支付）
3. 系统检查虚拟支付配置有效性（金额、门店、过期时间）
4. 扣减虚拟支付额度（乐观锁：UPDATE ... SET used_amount=used_amount+pay_amount WHERE id=? AND version=? AND used_amount+pay_amount<=total_amount）
5. 扣减成功 → 生成核销码
   - 格式：VP + 年月日(6位) + 随机码(8位)，如 VP260511AB3CD7F2
   - 使用SecureRandom生成随机部分
   - 唯一索引保障不重复
6. 核销码绑定指定门店 + 设置过期时间
7. 更新订单状态为"待核销"
8. 缓存核销码到Redis
9. 返回核销码信息（数字码 + 二维码URL）
```

### 5.2 门店代付模式流程

```
1. 用户选择商品 → 选择"门店代付" → 选择代付门店
2. 系统创建订单（状态：待付，pay_type=4门店代付）
3. 系统检查门店代付额度是否充足
   - 优先查Redis缓存，未命中查DB并回填缓存
   - 乐观锁扣减：UPDATE t_store_payment_quota SET used_quota=used_quota+amount, version=version+1 WHERE store_id=? AND version=? AND used_quota+amount<=total_quota
4. 扣减成功 → 生成核销码（绑定代付门店）
5. 更新订单状态为"待核销"
6. 返回核销码信息
```

### 5.3 扫码核销流程

```
1. 店员打开核销端 → 扫描用户核销码二维码
2. 系统获取核销码 → 尝试获取Redis分布式锁 verify:lock:{code}（SETNX，30秒过期）
3. 获取锁失败 → 返回"正在核销中，请稍后"
4. 获取锁成功 → 查询核销码状态
   - 优先查Redis缓存 verify:code:{code}
   - 未命中查DB并回填缓存
5. 校验：
   a. 码是否存在 → 不存在返回错误
   b. 状态是否为"未使用" → 已使用返回"该码已核销"
   c. 是否已过期 → 已过期返回"该码已过期"
   d. 是否本门店 → 非本门店返回"该码需在XX门店核销"
6. 更新核销码状态为"已使用"（乐观锁 + 分布式锁双重保障）
   UPDATE t_verification_code SET status=1, used_time=NOW(), verified_by=?, version=version+1 WHERE code=? AND version=? AND status=0
7. 更新订单状态为"已核销"
8. 删除Redis缓存 verify:code:{code}
9. 释放Redis锁
10. 记录操作日志
11. 返回核销成功
```

### 5.4 批量兑换码生成流程

```
1. 管理员后台 → 选择商品/金额 → 选择指定门店 → 输入生成数量
2. 系统创建批次记录（batch_no = BATCH + 时间戳 + 随机码）
3. 分批生成（每批1000条）：
   a. 生成1000个核销码（SecureRandom）
   b. 批量INSERT（INSERT INTO ... VALUES (...),(...),...）
   c. 提交事务
   d. Redis Pipeline批量缓存
4. 全部完成 → 返回批次统计（总数、成功数、失败数）
5. 失败的码记录到操作日志表 t_operation_log（module=batch_generate, action=fail, detail含失败原因和码信息），支持通过日志重试
```

### 5.5 订单过期处理流程

```
1. 定时任务每分钟扫描过期订单
   SELECT id FROM t_order WHERE order_status=1 AND expire_time<NOW()
2. 批量更新订单状态为"已过期"
3. 批量更新关联核销码状态为"已过期"
4. 虚拟支付订单：回退虚拟支付额度
5. 门店代付订单：回退门店代付额度
6. 清理Redis缓存
```

## 6. API设计

### 6.1 用户端API（/api/）

**门店模块**
| API | 方法 | 说明 | 认证 |
|-----|------|------|------|
| /api/store/list | GET | 门店列表（支持LBS排序） | 否 |
| /api/store/detail/{id} | GET | 门店详情 | 否 |

**商品模块**
| API | 方法 | 说明 | 认证 |
|-----|------|------|------|
| /api/product/list | GET | 商品列表（分页） | 否 |
| /api/product/detail/{id} | GET | 商品详情 | 否 |

**订单模块**
| API | 方法 | 说明 | 认证 |
|-----|------|------|------|
| /api/order/create | POST | 创建订单 | 是 |
| /api/order/list | GET | 我的订单列表 | 是 |
| /api/order/detail/{id} | GET | 订单详情 | 是 |
| /api/order/cancel/{id} | POST | 取消订单 | 是 |

**支付模块**
| API | 方法 | 说明 | 认证 |
|-----|------|------|------|
| /api/payment/pay | POST | 发起支付 | 是 |
| /api/payment/callback/wechat | POST | 微信支付回调 | 否(验签) |
| /api/payment/callback/alipay | POST | 支付宝回调 | 否(验签) |

**核销模块**
| API | 方法 | 说明 | 认证 |
|-----|------|------|------|
| /api/verify/code/{orderId} | GET | 获取核销码 | 是 |
| /api/verify/qrcode/{orderId} | GET | 获取核销二维码图片 | 是 |

**用户模块**
| API | 方法 | 说明 | 认证 |
|-----|------|------|------|
| /api/user/register | POST | 注册 | 否 |
| /api/user/login | POST | 登录 | 否 |
| /api/user/info | GET | 个人信息 | 是 |
| /api/user/update | PUT | 更新个人信息 | 是 |

### 6.2 管理后台API（/admin/）

**门店管理**
| API | 方法 | 说明 |
|-----|------|------|
| /admin/store/list | GET | 门店列表 |
| /admin/store/create | POST | 创建门店 |
| /admin/store/update | PUT | 更新门店 |
| /admin/store/delete/{id} | DELETE | 删除门店 |

**商品管理**
| API | 方法 | 说明 |
|-----|------|------|
| /admin/product/list | GET | 商品列表 |
| /admin/product/create | POST | 创建商品 |
| /admin/product/update | PUT | 更新商品 |
| /admin/product/bindStore | POST | 绑定商品到门店 |
| /admin/product/unbindStore | POST | 解绑商品门店 |

**虚拟支付管理**
| API | 方法 | 说明 |
|-----|------|------|
| /admin/virtual/list | GET | 虚拟支付配置列表 |
| /admin/virtual/config | POST | 创建虚拟支付配置 |
| /admin/virtual/update | PUT | 更新虚拟支付配置 |
| /admin/virtual/bindStore | POST | 指定虚拟支付适用门店 |

**门店代付额度管理**
| API | 方法 | 说明 |
|-----|------|------|
| /admin/store/quota/{storeId} | GET | 查询门店代付额度 |
| /admin/store/quota | POST | 设置门店代付额度 |
| /admin/store/quota/update | PUT | 更新门店代付额度 |

**核销码管理**
| API | 方法 | 说明 |
|-----|------|------|
| /admin/verify/batchGenerate | POST | 批量生成核销码 |
| /admin/verify/list | GET | 核销码列表 |
| /admin/verify/scan | POST | 扫码核销 |
| /admin/verify/manual | POST | 手动输入核销 |
| /admin/verify/void/{code} | POST | 作废核销码 |

**订单管理**
| API | 方法 | 说明 |
|-----|------|------|
| /admin/order/list | GET | 订单列表 |
| /admin/order/detail/{id} | GET | 订单详情 |
| /admin/order/refund/{id} | POST | 退款 |

**管理员认证**
| API | 方法 | 说明 |
|-----|------|------|
| /admin/auth/login | POST | 管理员登录 |
| /admin/auth/info | GET | 当前管理员信息 |

## 7. 安全设计

### 7.1 支付安全
- 所有支付接口强制HTTPS
- 支付回调验签（微信/支付宝签名验证）
- 虚拟支付额度操作使用数据库事务 + 乐观锁
- 订单金额服务端校验，前端传价仅作展示
- 支付回调幂等处理（订单号唯一索引 + 状态校验）

### 7.2 核销码安全
- 核销码生成使用SecureRandom加密随机数
- 核销码不可预测、不可枚举
- 核销操作需店员身份认证（JWT + 角色权限）
- 同一核销码30秒内仅允许一次核销请求（分布式锁 + 限流）
- 核销码使用记录不可删除，仅可作废（需管理员权限）

### 7.3 接口安全
- JWT Token认证 + 刷新机制（Access Token 2小时 + Refresh Token 7天）
- 接口限流（Redis + 滑动窗口，普通接口60次/分钟，支付接口10次/分钟）
- 敏感操作二次验证（支付、核销需验证码或密码）
- SQL注入防护（MyBatis-Plus参数化查询）
- XSS防护（输入过滤 + 输出编码）
- CORS白名单配置

### 7.4 数据安全
- 用户密码BCrypt加密（强度10）
- 手机号等敏感信息脱敏展示（138****1234）
- 操作日志审计（支付、核销、退款等关键操作记录）
- 核销码使用记录不可删除，仅可作废
- 数据库定期备份策略

## 8. MVP迭代计划

### Phase 1 - 核心基础（MVP）
- 用户注册/登录
- 门店管理（CRUD）
- 商品管理（CRUD + 门店绑定）
- 订单创建/查询
- 虚拟支付 + 核销码生成
- 扫码核销
- 门店代付模式

### Phase 2 - 支付完善
- 微信支付接入
- 支付宝支付接入
- 退款功能

### Phase 3 - 体验优化
- LBS门店推荐
- 商品分类/搜索
- 订单通知（短信/微信模板消息）
- 核销码批量导出

### Phase 4 - 运营工具
- 数据统计看板
- 营销活动（优惠券、满减）
- 会员体系
