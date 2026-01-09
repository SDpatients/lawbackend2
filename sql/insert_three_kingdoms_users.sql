-- 插入十个三国时期人物测试数据
-- 包含: tb_user 和 tb_user_role 表

-- 插入用户数据
INSERT INTO tb_user (username, password, real_name, mobile, email, phone, is_valid, status, login_type) VALUES
('liubei', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '刘备', '13800001001', 'liubei@shu.com', '010-88880001', '1', 'ACTIVE', '1'),
('guanyu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '关羽', '13800001002', 'guanyu@shu.com', '010-88880002', '1', 'ACTIVE', '1'),
('zhangfei', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张飞', '13800001003', 'zhangfei@shu.com', '010-88880003', '1', 'ACTIVE', '1'),
('zhaoyun', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '赵云', '13800001004', 'zhaoyun@shu.com', '010-88880004', '1', 'ACTIVE', '1'),
('caocao', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '曹操', '13800001005', 'caocao@wei.com', '010-88880005', '1', 'ACTIVE', '1'),
('simayi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '司马懿', '13800001006', 'simayi@wei.com', '010-88880006', '1', 'ACTIVE', '1'),
('dianwei', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '典韦', '13800001007', 'dianwei@wei.com', '010-88880007', '1', 'ACTIVE', '1'),
('sunquan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '孙权', '13800001008', 'sunquan@wu.com', '010-88880008', '1', 'ACTIVE', '1'),
('zhouyu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '周瑜', '13800001009', 'zhouyu@wu.com', '010-88880009', '1', 'ACTIVE', '1'),
('luxun', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '陆逊', '13800001010', 'luxun@wu.com', '010-88880010', '1', 'ACTIVE', '1');

-- 插入用户角色关联数据
-- 假设角色ID: 1-管理员, 2-律师, 3-助理, 4-普通用户
-- 请根据实际数据库中的角色ID调整role_id

-- 蜀国人物
INSERT INTO tb_user_role (user_id, role_id) VALUES
((SELECT id FROM tb_user WHERE username = 'liubei'), 1),
((SELECT id FROM tb_user WHERE username = 'guanyu'), 2),
((SELECT id FROM tb_user WHERE username = 'zhangfei'), 2),
((SELECT id FROM tb_user WHERE username = 'zhaoyun'), 2);

-- 魏国人物
INSERT INTO tb_user_role (user_id, role_id) VALUES
((SELECT id FROM tb_user WHERE username = 'caocao'), 1),
((SELECT id FROM tb_user WHERE username = 'simayi'), 2),
((SELECT id FROM tb_user WHERE username = 'dianwei'), 3);

-- 吴国人物
INSERT INTO tb_user_role (user_id, role_id) VALUES
((SELECT id FROM tb_user WHERE username = 'sunquan'), 1),
((SELECT id FROM tb_user WHERE username = 'zhouyu'), 2),
((SELECT id FROM tb_user WHERE username = 'luxun'), 2);
