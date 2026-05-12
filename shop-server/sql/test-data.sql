USE shopx;

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE t_operation_log;
TRUNCATE TABLE t_verification_code;
TRUNCATE TABLE t_order_item;
TRUNCATE TABLE t_order;
TRUNCATE TABLE t_product_store;
TRUNCATE TABLE t_product;
TRUNCATE TABLE t_store_payment_quota;
TRUNCATE TABLE t_virtual_payment_config;
TRUNCATE TABLE t_user;
TRUNCATE TABLE t_admin;
TRUNCATE TABLE t_store;

SET FOREIGN_KEY_CHECKS = 1;

-- ========================================
-- 1. 门店模块 (5家门店，含不同状态)
-- ========================================
INSERT INTO t_store (store_name, store_code, address, contact_phone, business_hours, status, longitude, latitude) VALUES
('旗舰店', 'STORE001', '北京市朝阳区建国路88号', '010-88880001', '09:00-22:00', 1, 116.461000, 39.908000),
('中心店', 'STORE002', '北京市海淀区中关村大街66号', '010-88880002', '09:00-21:30', 1, 116.316000, 39.982000),
('望京店', 'STORE003', '北京市朝阳区望京西路10号', '010-88880003', '10:00-21:00', 1, 116.470400, 39.993000),
('西单店', 'STORE004', '北京市西城区西单北大街120号', '010-88880004', '09:30-22:00', 1, 116.373000, 39.913000),
('通州店', 'STORE005', '北京市通州区新华西街58号', '010-88880005', '09:00-21:00', 0, 116.656000, 39.902000);

-- ========================================
-- 2. 商品模块 (8个商品，含不同门店范围和状态)
-- ========================================
INSERT INTO t_product (product_name, price, original_price, stock, description, images, store_scope, status) VALUES
('精品礼盒A', 299.00, 399.00, 500, '精选优质商品，精美礼盒包装，适合送礼', '["https://example.com/images/product_a_1.jpg", "https://example.com/images/product_a_2.jpg"]', 0, 1),
('尊享套装B', 599.00, 799.00, 300, '尊享品质，限量发售，尊贵之选', '["https://example.com/images/product_b_1.jpg", "https://example.com/images/product_b_2.jpg"]', 0, 1),
('健康养生礼盒C', 168.00, 238.00, 800, '天然健康，养生佳品，老少皆宜', '["https://example.com/images/product_c_1.jpg"]', 0, 1),
('数码精品D', 1299.00, 1599.00, 150, '科技前沿，品质生活，智能体验', '["https://example.com/images/product_d_1.jpg", "https://example.com/images/product_d_2.jpg"]', 1, 1),
('家居好物E', 89.00, 129.00, 2000, '温馨家居，品质生活，超值之选', '["https://example.com/images/product_e_1.jpg"]', 0, 1),
('美食大礼包F', 358.00, 498.00, 600, '精选美食，味蕾盛宴，节日必备', '["https://example.com/images/product_f_1.jpg", "https://example.com/images/product_f_2.jpg"]', 1, 1),
('运动装备G', 459.00, 599.00, 250, '专业运动装备，品质保障', '["https://example.com/images/product_g_1.jpg"]', 0, 1),
('已下架商品H', 199.00, 299.00, 0, '此商品已下架', '["https://example.com/images/product_h_1.jpg"]', 0, 0);

-- ========================================
-- 3. 商品-门店关联 (store_scope=1的商品指定门店)
-- ========================================
INSERT INTO t_product_store (product_id, store_id) VALUES
(4, 1),
(4, 2),
(4, 3),
(6, 1),
(6, 3),
(6, 4);

-- ========================================
-- 4. 用户模块 (6个用户，含不同状态)
-- ========================================
INSERT INTO t_user (phone, password, nickname, avatar, status) VALUES
('13800001111', '$2a$10$zbTHCnAXCZbLlvghV3eg9ugDlWGVUX1ggXtHJS3yyd/g..T3EjOIu', '张三', 'https://example.com/avatar/user1.jpg', 1),
('13800002222', '$2a$10$zbTHCnAXCZbLlvghV3eg9ugDlWGVUX1ggXtHJS3yyd/g..T3EjOIu', '李四', 'https://example.com/avatar/user2.jpg', 1),
('13800003333', '$2a$10$zbTHCnAXCZbLlvghV3eg9ugDlWGVUX1ggXtHJS3yyd/g..T3EjOIu', '王五', 'https://example.com/avatar/user3.jpg', 1),
('13800004444', '$2a$10$zbTHCnAXCZbLlvghV3eg9ugDlWGVUX1ggXtHJS3yyd/g..T3EjOIu', '赵六', 'https://example.com/avatar/user4.jpg', 1),
('13800005555', '$2a$10$zbTHCnAXCZbLlvghV3eg9ugDlWGVUX1ggXtHJS3yyd/g..T3EjOIu', '孙七', 'https://example.com/avatar/user5.jpg', 1),
('13800006666', '$2a$10$zbTHCnAXCZbLlvghV3eg9ugDlWGVUX1ggXtHJS3yyd/g..T3EjOIu', '周八', 'https://example.com/avatar/user6.jpg', 0);

-- ========================================
-- 5. 管理员模块 (4个管理员，不同角色和门店)
-- ========================================
INSERT INTO t_admin (username, password, real_name, phone, role, store_id, status) VALUES
('admin', '$2a$10$zbTHCnAXCZbLlvghV3eg9ugDlWGVUX1ggXtHJS3yyd/g..T3EjOIu', '超级管理员', '13800000000', 1, NULL, 1),
('store_admin_1', '$2a$10$zbTHCnAXCZbLlvghV3eg9ugDlWGVUX1ggXtHJS3yyd/g..T3EjOIu', '旗舰店店长', '13800000001', 2, 1, 1),
('store_admin_2', '$2a$10$zbTHCnAXCZbLlvghV3eg9ugDlWGVUX1ggXtHJS3yyd/g..T3EjOIu', '中心店店长', '13800000002', 2, 2, 1),
('store_admin_3', '$2a$10$zbTHCnAXCZbLlvghV3eg9ugDlWGVUX1ggXtHJS3yyd/g..T3EjOIu', '望京店店长', '13800000003', 2, 3, 1);

-- ========================================
-- 6. 虚拟支付配置 (3条，不同门店和状态)
-- ========================================
INSERT INTO t_virtual_payment_config (config_name, total_amount, used_amount, store_id, status, expire_time, version) VALUES
('2026年度全店虚拟支付额度', 100000.00, 0.00, 0, 1, '2026-12-31 23:59:59', 0),
('2026年度旗舰店专属额度', 30000.00, 0.00, 1, 1, '2026-12-31 23:59:59', 0),
('2025年度过期额度', 50000.00, 50000.00, 0, 0, '2025-12-31 23:59:59', 0);

-- ========================================
-- 7. 门店代付额度 (4家门店)
-- ========================================
INSERT INTO t_store_payment_quota (store_id, total_quota, used_quota, status, version) VALUES
(1, 50000.00, 0.00, 1, 0),
(2, 50000.00, 0.00, 1, 0),
(3, 30000.00, 0.00, 1, 0),
(4, 20000.00, 0.00, 1, 0);

-- ========================================
-- 8. 订单模块 (12个订单，覆盖所有状态和支付类型)
-- ========================================

-- 用户1的订单 (多种状态)
INSERT INTO t_order (order_no, user_id, store_id, total_amount, pay_amount, pay_type, pay_status, order_status, proxy_store_id, remark, expire_time, pay_time, created_at) VALUES
('ORD20260501001', 1, 1, 299.00, 299.00, 3, 1, 2, NULL, '虚拟支付已核销', '2026-05-19 23:59:59', '2026-05-01 10:30:00', '2026-05-01 10:28:00'),
('ORD20260502001', 1, 2, 599.00, 599.00, 4, 1, 2, 2, '门店代付已核销', '2026-05-19 23:59:59', '2026-05-02 14:20:00', '2026-05-02 14:18:00'),
('ORD20260503001', 1, 1, 598.00, 598.00, 3, 1, 1, NULL, '虚拟支付待核销', '2026-05-19 23:59:59', '2026-05-03 09:15:00', '2026-05-03 09:12:00'),
('ORD20260504001', 1, 3, 1299.00, 1299.00, 3, 1, 1, NULL, '望京店待核销', '2026-05-19 23:59:59', '2026-05-04 16:45:00', '2026-05-04 16:40:00'),
('ORD20260505001', 1, 1, 89.00, 89.00, 3, 0, 0, NULL, '未支付订单', '2026-05-19 23:59:59', NULL, '2026-05-05 11:00:00'),
('ORD20260506001', 1, 2, 358.00, 358.00, 4, 0, 0, 1, '代付未支付', '2026-05-19 23:59:59', NULL, '2026-05-06 08:30:00');

-- 用户2的订单
INSERT INTO t_order (order_no, user_id, store_id, total_amount, pay_amount, pay_type, pay_status, order_status, proxy_store_id, remark, expire_time, pay_time, created_at) VALUES
('ORD20260502002', 2, 1, 168.00, 168.00, 3, 1, 2, NULL, NULL, '2026-05-19 23:59:59', '2026-05-02 11:00:00', '2026-05-02 10:55:00'),
('ORD20260503002', 2, 3, 918.00, 918.00, 3, 1, 1, NULL, '望京店虚拟支付', '2026-05-19 23:59:59', '2026-05-03 15:30:00', '2026-05-03 15:25:00'),
('ORD20260504002', 2, 4, 459.00, 459.00, 4, 1, 1, 3, '西单店代付望京店', '2026-05-19 23:59:59', '2026-05-04 10:20:00', '2026-05-04 10:15:00');

-- 用户3的订单
INSERT INTO t_order (order_no, user_id, store_id, total_amount, pay_amount, pay_type, pay_status, order_status, proxy_store_id, remark, expire_time, pay_time, created_at) VALUES
('ORD20260503003', 3, 2, 1198.00, 1198.00, 3, 1, 2, NULL, NULL, '2026-05-19 23:59:59', '2026-05-03 18:00:00', '2026-05-03 17:50:00'),
('ORD20260505002', 3, 1, 716.00, 716.00, 4, 1, 1, 2, '中心店代付', '2026-05-19 23:59:59', '2026-05-05 13:40:00', '2026-05-05 13:35:00');

-- 用户4的订单 (已取消)
INSERT INTO t_order (order_no, user_id, store_id, total_amount, pay_amount, pay_type, pay_status, order_status, proxy_store_id, remark, expire_time, pay_time, created_at) VALUES
('ORD20260505003', 4, 1, 299.00, 299.00, 3, 0, 3, NULL, '用户取消', '2026-05-19 23:59:59', NULL, '2026-05-05 20:10:00');

-- ========================================
-- 9. 订单明细
-- ========================================
INSERT INTO t_order_item (order_id, product_id, product_name, price, quantity) VALUES
(1, 1, '精品礼盒A', 299.00, 1),
(2, 2, '尊享套装B', 599.00, 1),
(3, 1, '精品礼盒A', 299.00, 2),
(4, 4, '数码精品D', 1299.00, 1),
(5, 5, '家居好物E', 89.00, 1),
(6, 6, '美食大礼包F', 358.00, 1),
(7, 3, '健康养生礼盒C', 168.00, 1),
(8, 4, '数码精品D', 1299.00, 1),
(8, 5, '家居好物E', 89.00, 1),
(9, 5, '家居好物E', 459.00, 1),
(10, 2, '尊享套装B', 599.00, 2),
(11, 6, '美食大礼包F', 358.00, 2),
(12, 1, '精品礼盒A', 299.00, 1);

-- ========================================
-- 10. 核销码模块 (覆盖所有状态)
-- ========================================
INSERT INTO t_verification_code (code, order_id, order_no, user_id, store_id, status, used_time, verified_by, expire_time, batch_no, version) VALUES
('VP260501A1B2C3D4', 1, 'ORD20260501001', 1, 1, 1, '2026-05-01 11:00:00', 2, '2026-05-19 23:59:59', NULL, 1),
('VP260502E5F6G7H8', 2, 'ORD20260502001', 1, 2, 1, '2026-05-02 15:00:00', 3, '2026-05-19 23:59:59', NULL, 1),
('VP260503I9J0K1L2', 3, 'ORD20260503001', 1, 1, 0, NULL, NULL, '2026-05-19 23:59:59', NULL, 0),
('VP260504M3N4O5P6', 4, 'ORD20260504001', 1, 3, 0, NULL, NULL, '2026-05-19 23:59:59', NULL, 0),
('VP260502Q7R8S9T0', 7, 'ORD20260502002', 2, 1, 1, '2026-05-02 11:30:00', 2, '2026-05-19 23:59:59', NULL, 1),
('VP260503U1V2W3X4', 8, 'ORD20260503002', 2, 3, 0, NULL, NULL, '2026-05-19 23:59:59', NULL, 0),
('VP260504Y5Z6A7B8', 9, 'ORD20260504002', 2, 3, 0, NULL, NULL, '2026-05-19 23:59:59', NULL, 0),
('VP260503C9D0E1F2', 10, 'ORD20260503003', 3, 2, 1, '2026-05-03 18:30:00', 3, '2026-05-19 23:59:59', NULL, 1),
('VP260505G3H4I5J6', 11, 'ORD20260505002', 3, 2, 0, NULL, NULL, '2026-05-19 23:59:59', NULL, 0),
('VP260505K7L8M9N0', 12, 'ORD20260505003', 4, 1, 2, NULL, NULL, '2025-01-01 00:00:00', NULL, 0);

-- ========================================
-- 11. 操作日志
-- ========================================
INSERT INTO t_operation_log (operator_id, operator_type, module, action, detail, ip, created_at) VALUES
(1, 1, 'ORDER', 'CREATE', '创建订单 ORD20260501001', '192.168.1.100', '2026-05-01 10:28:00'),
(2, 2, 'VERIFY', 'SCAN', '扫码核销 VP260501A1B2C3D4', '192.168.1.101', '2026-05-01 11:00:00'),
(1, 1, 'ORDER', 'CREATE', '创建订单 ORD20260502001', '192.168.1.100', '2026-05-02 14:18:00'),
(3, 2, 'VERIFY', 'MANUAL', '手动核销 VP260502E5F6G7H8', '192.168.1.102', '2026-05-02 15:00:00'),
(1, 1, 'PAYMENT', 'VIRTUAL_PAY', '虚拟支付订单 ORD20260503001', '192.168.1.100', '2026-05-03 09:15:00'),
(2, 2, 'VERIFY', 'SCAN', '扫码核销 VP260502Q7R8S9T0', '192.168.1.101', '2026-05-02 11:30:00'),
(1, 1, 'ADMIN', 'LOGIN', '管理员登录', '192.168.1.200', '2026-05-01 08:00:00'),
(2, 2, 'ADMIN', 'LOGIN', '门店管理员登录', '192.168.1.201', '2026-05-01 08:05:00'),
(1, 1, 'PRODUCT', 'UPDATE', '修改商品 精品礼盒A 价格', '192.168.1.200', '2026-05-02 09:00:00'),
(1, 1, 'STORE', 'UPDATE', '修改门店 通州店 状态为禁用', '192.168.1.200', '2026-05-03 10:00:00');
