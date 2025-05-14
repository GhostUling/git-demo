-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS stream;
USE stream;

-- 删除现有表（如果存在），按照外键约束顺序
DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS player_game;
DROP TABLE IF EXISTS player_game_library;
DROP TABLE IF EXISTS game_upload;
DROP TABLE IF EXISTS email_verification;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS game;
DROP TABLE IF EXISTS developer;
DROP TABLE IF EXISTS player;

-- 创建玩家表
CREATE TABLE player (
    player_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    registration_time DATETIME(6) NOT NULL,
    balance DECIMAL(38, 2) DEFAULT 0.00,
    avatar_url VARCHAR(255),
    account_active BIT(1) NOT NULL DEFAULT 1
);

-- 创建开发者表
CREATE TABLE developer (
    developer_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    developer_name VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255) NOT NULL,
    username VARCHAR(255),
    password VARCHAR(255),
    company_info VARCHAR(255),
    company_logo VARCHAR(255),
    verified BIT(1) NOT NULL DEFAULT 0
);

-- 创建游戏表
CREATE TABLE game (
    game_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    game_name VARCHAR(255) NOT NULL,
    game_type VARCHAR(255),
    price DECIMAL(38, 2) NOT NULL,
    description VARCHAR(255),
    install_package_path VARCHAR(255),
    developer_id INT,
    FOREIGN KEY (developer_id) REFERENCES developer(developer_id)
);

-- 创建玩家-游戏关联表（玩家拥有的游戏）
CREATE TABLE player_game (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    player_id INT,
    game_id INT,
    purchase_date DATETIME(6),
    play_time_minutes BIGINT NOT NULL DEFAULT 0,
    last_played_date DATETIME(6),
    FOREIGN KEY (player_id) REFERENCES player(player_id),
    FOREIGN KEY (game_id) REFERENCES game(game_id)
);

-- 创建交易表
CREATE TABLE transaction (
    transaction_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    player_id INT,
    game_id INT,
    amount DECIMAL(38, 2),
    transaction_time DATETIME(6),
    payment_status TINYINT,
    FOREIGN KEY (player_id) REFERENCES player(player_id),
    FOREIGN KEY (game_id) REFERENCES game(game_id)
);

-- 创建邮箱验证表
CREATE TABLE email_verification (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    verification_code VARCHAR(10) NOT NULL,
    expiry_time DATETIME(6) NOT NULL,
    is_verified BIT(1) NOT NULL DEFAULT 0
);

-- 创建游戏上传表
CREATE TABLE game_upload (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    developer_id INT NOT NULL,
    game_name VARCHAR(255) NOT NULL,
    version VARCHAR(50) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    file_type VARCHAR(100) NOT NULL,
    upload_time DATETIME(6) NOT NULL,
    status VARCHAR(20) NOT NULL,
    description TEXT,
    game_id INT,
    FOREIGN KEY (developer_id) REFERENCES developer(developer_id)
);

-- 创建玩家游戏库表
CREATE TABLE player_game_library (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    player_id INT NOT NULL,
    game_id INT NOT NULL,
    purchase_time DATETIME(6) NOT NULL,
    last_play_time DATETIME(6),
    play_time INT DEFAULT 0,
    is_favorite BIT(1) DEFAULT 0,
    notes VARCHAR(500),
    FOREIGN KEY (player_id) REFERENCES player(player_id),
    FOREIGN KEY (game_id) REFERENCES game(game_id)
);

-- 创建订单表
CREATE TABLE orders (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    player_id INT NOT NULL,
    game_id INT NOT NULL,
    amount DECIMAL(38, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    create_time DATETIME(6) NOT NULL,
    pay_time DATETIME(6),
    transaction_id VARCHAR(100),
    description VARCHAR(500),
    FOREIGN KEY (player_id) REFERENCES player(player_id),
    FOREIGN KEY (game_id) REFERENCES game(game_id)
);

-- 插入测试数据
-- 插入开发者
INSERT INTO developer (developer_name, contact_email, username, password, company_info, verified) VALUES 
    ('Cyber Games', 'contact@cybergames.com', 'cyber_games', 'password123', '一家科技游戏开发公司', 1),
    ('DMC Studio', 'info@dmcstudio.com', 'dmc_studio', 'password123', '专注于动作游戏的开发工作室', 1),
    ('miHoYo', 'contact@mihoyo.com', 'mihoyo', 'password123', '知名游戏开发商', 1),
    ('腾讯游戏', 'game@tencent.com', 'tencent', 'password123', '中国最大的游戏公司之一', 1),
    ('CD Projekt Red', 'info@cdprojektred.com', 'cdpr', 'password123', '波兰游戏开发商，知名作品《巫师》系列', 1),
    ('FromSoftware', 'contact@fromsoftware.jp', 'fromsoftware', 'password123', '日本游戏开发商，黑魂系列开发者', 1),
    ('Santa Monica Studio', 'info@sms.com', 'santamonica', 'password123', '索尼第一方工作室，战神系列开发商', 1),
    ('Rockstar Games', 'info@rockstargames.com', 'rockstar', 'password123', '美国游戏开发商，GTA和荒野大镖客开发商', 1),
    ('Respawn Entertainment', 'contact@respawn.com', 'respawn', 'password123', 'EA旗下工作室，泰坦陨落和星战绝地开发商', 1),
    ('24Entertainment', 'contact@24entertainment.com', '24entertainment', 'password123', '永劫无间开发商', 1);

-- 插入游戏（使用显式指定ID）
ALTER TABLE game AUTO_INCREMENT = 1;
INSERT INTO game (game_id, game_name, game_type, price, description, developer_id) VALUES 
    (1, 'Cyber Adventure 2077', '角色扮演', 198.00, '一款开放世界角色扮演游戏，设定在未来的赛博朋克世界。', 1),
    (2, 'Devil May Cry 5', '动作冒险', 168.00, '一款充满爽快战斗体验的动作冒险游戏。', 2),
    (3, 'genshin-impact', '开放世界', 68.00, '一款开放世界的冒险游戏，拥有元素魔法系统。', 3),
    (4, 'The Witcher 3: Wild Hunt', '角色扮演', 149.00, '一款著名的开放世界角色扮演游戏，玩家扮演猎魔人杰洛特展开冒险。', 5),
    (5, 'Naraka Bladepoint', '动作', 98.00, '一款东方武侠风格的多人在线战斗游戏，强调近战格斗和机动性。', 10),
    (6, 'Elden Ring', '动作角色扮演', 298.00, '由宫崎英高和乔治·R·R·马丁合作的开放世界动作角色扮演游戏。', 6),
    (7, 'God of War', '动作冒险', 279.00, '北欧神话背景下的克雷多斯和儿子阿特柔斯的冒险故事。', 7),
    (8, 'Red Dead Redemption 2', '开放世界', 249.00, '美国西部背景的开放世界游戏，讲述亚瑟·摩根的故事。', 8),
    (9, 'Star Wars Jedi Fallen Order', '动作冒险', 198.00, '星球大战宇宙中的单人动作冒险游戏，讲述绝地武士的故事。', 9);

-- 插入测试用户
INSERT INTO player (username, password, email, registration_time, balance, account_active) VALUES 
    ('testuser', 'password123', 'test@example.com', NOW(), 500.00, 1); 
    INSERT INTO player (username, password, email, registration_time, balance, account_active) VALUES 
    ('111111', '111111', '111111@example.com', NOW(), 500.00, 1); 