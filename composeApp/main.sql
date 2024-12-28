/*
 Navicat Premium Dump SQL

 Source Server         : penny
 Source Server Type    : SQLite
 Source Server Version : 3045000 (3.45.0)
 Source Schema         : main

 Target Server Type    : SQLite
 Target Server Version : 3045000 (3.45.0)
 File Encoding         : 65001

 Date: 28/12/2024 10:18:42
*/

PRAGMA foreign_keys = false;

-- ----------------------------
-- Table structure for ledgerEntity
-- ----------------------------
DROP TABLE IF EXISTS "ledgerEntity";
CREATE TABLE ledgerEntity (
    uuid TEXT PRIMARY KEY,                      -- 账本UUID，设为主键
    user_uuid TEXT NOT NULL,                    -- 用户UUID，外键
    name TEXT NOT NULL,                         -- 账本名称
    currency_code TEXT NOT NULL,                -- 货币代码
    cover_name TEXT NOT NULL,                   -- 封面名称（枚举名）
    description TEXT,                           -- 描述
    created_at INTEGER NOT NULL DEFAULT (strftime('%s','now')),   -- 创建时间
    updated_at INTEGER NOT NULL DEFAULT (strftime('%s','now')),    -- 修改时间

    FOREIGN KEY (user_uuid) REFERENCES userEntity(uuid)
);

-- ----------------------------
-- Records of ledgerEntity
-- ----------------------------
BEGIN;
INSERT INTO "ledgerEntity" ("uuid", "user_uuid", "name", "currency_code", "cover_name", "description", "created_at", "updated_at") VALUES ('1929ced0-671f-4042-b05b-f6d82bd9e43c', 'b1931c5c-4827-4599-92a8-b188fd902ac4', '1日	1', 'USD', 'DEFAULT', '', 1735293609, 1735293609);
COMMIT;

-- ----------------------------
-- Table structure for userEntity
-- ----------------------------
DROP TABLE IF EXISTS "userEntity";
CREATE TABLE userEntity (
    uuid TEXT PRIMARY KEY,                      -- 用户UUID，设为主键
    username TEXT NOT NULL,                     -- 用户名
    email TEXT  UNIQUE,                 -- 邮箱，唯一
    created_at INTEGER NOT NULL DEFAULT (strftime('%s','now')),   -- 创建时间
    updated_at INTEGER NOT NULL DEFAULT (strftime('%s','now'))    -- 修改时间
);

-- ----------------------------
-- Records of userEntity
-- ----------------------------
BEGIN;
INSERT INTO "userEntity" ("uuid", "username", "email", "created_at", "updated_at") VALUES ('b1931c5c-4827-4599-92a8-b188fd902ac4', '', 'aaa@gmail.com', 1735293605, 1735294043);
COMMIT;

PRAGMA foreign_keys = true;
