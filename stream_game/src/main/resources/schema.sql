-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS stream;
USE stream;

-- 删除现有表（如果存在），按照外键约束顺序
DROP TABLE IF EXISTS transaction;
DROP TABLE IF EXISTS player_game;
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

-- 插入测试数据
-- 插入开发者
INSERT INTO developer (developer_name, contact_email, username, password, company_info, verified) VALUES 
    ('Cyber Games', 'contact@cybergames.com', 'cyber_games', 'password123', '一家科技游戏开发公司', 1),
    ('DMC Studio', 'info@dmcstudio.com', 'dmc_studio', 'password123', '专注于动作游戏的开发工作室', 1),
    ('miHoYo', 'contact@mihoyo.com', 'mihoyo', 'password123', '知名游戏开发商', 1),
    ('腾讯游戏', 'game@tencent.com', 'tencent', 'password123', '中国最大的游戏公司之一', 1);

-- 插入游戏
INSERT INTO game (game_name, game_type, price, description, developer_id) VALUES 
    ('Cyber Adventure 2077', '角色扮演', 198.00, '一款开放世界角色扮演游戏，设定在未来的赛博朋克世界。', 1),
    ('Devil May Cry 5', '动作冒险', 168.00, '一款充满爽快战斗体验的动作冒险游戏。', 2),
    ('genshin-impact', '开放世界', 68.00, '一款开放世界的冒险游戏，拥有元素魔法系统。', 3),
    ('王者荣耀', '多人在线竞技', 6.00, '中国最受欢迎的多人在线竞技游戏之一。', 4);

-- 插入测试用户
INSERT INTO player (username, password, email, registration_time, balance, account_active) VALUES 
    ('testuser', 'password123', 'test@example.com', NOW(), 500.00, 1); 