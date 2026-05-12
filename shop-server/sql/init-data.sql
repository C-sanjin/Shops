USE shopx;

INSERT INTO t_admin (username, password, real_name, phone, role, store_id, status) VALUES
('admin', '$2a$10$zbTHCnAXCZbLlvghV3eg9ugDlWGVUX1ggXtHJS3yyd/g..T3EjOIu', '超级管理员', '13800000000', 1, NULL, 1);

INSERT INTO t_store (store_name, store_code, address, contact_phone, business_hours, status, longitude, latitude) VALUES
('旗舰店', 'STORE001', '北京市朝阳区建国路88号', '010-88880001', '09:00-22:00', 1, 116.461000, 39.908000),
('中心店', 'STORE002', '北京市海淀区中关村大街66号', '010-88880002', '09:00-21:30', 1, 116.316000, 39.982000);

INSERT INTO t_product (product_name, price, original_price, stock, description, images, store_scope, status) VALUES
('精品礼盒A', 299.00, 399.00, 500, '精选优质商品，精美礼盒包装', '["https://example.com/images/product_a_1.jpg", "https://example.com/images/product_a_2.jpg"]', 0, 1),
('尊享套装B', 599.00, 799.00, 300, '尊享品质，限量发售', '["https://example.com/images/product_b_1.jpg", "https://example.com/images/product_b_2.jpg"]', 0, 1);

INSERT INTO t_virtual_payment_config (config_name, total_amount, used_amount, store_id, status, expire_time, version) VALUES
('2026年度虚拟支付额度', 100000.00, 0.00, 0, 1, '2026-12-31 23:59:59', 0);

INSERT INTO t_store_payment_quota (store_id, total_quota, used_quota, status, version) VALUES
(1, 50000.00, 0.00, 1, 0),
(2, 50000.00, 0.00, 1, 0);
