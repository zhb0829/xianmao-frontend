/*
 Navicat Premium Data Transfer

 Source Server         : js
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : localhost:3306
 Source Schema         : transaction_sql

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 05/04/2023 20:18:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for audit
-- ----------------------------
DROP TABLE IF EXISTS `audit`;
CREATE TABLE `audit`  (
  `ID` int(0) NOT NULL AUTO_INCREMENT,
  `CommodityID` int(0) NOT NULL COMMENT '商品ID',
  `AuditStatus` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '审核状态',
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `CommodityID`(`CommodityID`) USING BTREE,
  CONSTRAINT `CommodID` FOREIGN KEY (`CommodityID`) REFERENCES `commodityinfo` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of audit
-- ----------------------------
INSERT INTO `audit` VALUES (1, 1, '已购买');

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `ID` int(0) NOT NULL AUTO_INCREMENT,
  `Category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类别',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE INDEX `c1`(`Category`) USING BTREE,
  INDEX `Category`(`Category`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (11, '化妆品');
INSERT INTO `category` VALUES (8, '图书');
INSERT INTO `category` VALUES (10, '宠物用品');
INSERT INTO `category` VALUES (12, '家居饰品');
INSERT INTO `category` VALUES (6, '家电');
INSERT INTO `category` VALUES (15, '数码产品');
INSERT INTO `category` VALUES (14, '文具');
INSERT INTO `category` VALUES (5, '日用品');
INSERT INTO `category` VALUES (7, '服饰');
INSERT INTO `category` VALUES (3, '水果');
INSERT INTO `category` VALUES (17, '玩具');
INSERT INTO `category` VALUES (4, '蔬菜');
INSERT INTO `category` VALUES (13, '运动用品');
INSERT INTO `category` VALUES (9, '酒类');
INSERT INTO `category` VALUES (1, '零食');
INSERT INTO `category` VALUES (16, '食品');

-- ----------------------------
-- Table structure for collect
-- ----------------------------
DROP TABLE IF EXISTS `collect`;
CREATE TABLE `collect`  (
  `ID` int(0) NOT NULL AUTO_INCREMENT,
  `UserID` int(0) NOT NULL COMMENT '用户ID',
  `CommodityID` int(0) NOT NULL COMMENT '商品ID',
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `UserID`(`UserID`) USING BTREE,
  INDEX `CommodityID`(`CommodityID`) USING BTREE,
  CONSTRAINT `CommodityCollectID` FOREIGN KEY (`CommodityID`) REFERENCES `commodityinfo` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `UserCollectID` FOREIGN KEY (`UserID`) REFERENCES `userlog` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of collect
-- ----------------------------
INSERT INTO `collect` VALUES (5, 18, 1);

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `ID` int(0) NOT NULL AUTO_INCREMENT,
  `CommodityID` int(0) NOT NULL COMMENT '商品ID',
  `UserID` int(0) NOT NULL COMMENT '用户ID',
  `CommentContent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `Time` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '评论时间',
  `Status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论状态',
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `CommodityID`(`CommodityID`) USING BTREE,
  INDEX `UserID`(`UserID`) USING BTREE,
  CONSTRAINT `CommodityID` FOREIGN KEY (`CommodityID`) REFERENCES `commodityinfo` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `UserID` FOREIGN KEY (`UserID`) REFERENCES `userlog` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (1, 1, 12, '很不错！孩子很喜欢', '2023-04-05 09:03:33', '真不错');

-- ----------------------------
-- Table structure for commodityinfo
-- ----------------------------
DROP TABLE IF EXISTS `commodityinfo`;
CREATE TABLE `commodityinfo`  (
  `ID` int(0) NOT NULL AUTO_INCREMENT,
  `TradeName` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品名称',
  `Price` double(8, 2) NULL DEFAULT NULL COMMENT '价格',
  `Description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品描述',
  `Time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '发货时间',
  `SellerID` int(0) NULL DEFAULT NULL COMMENT '卖家ID',
  `ShippingAddress` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发货地址',
  `Category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '种类',
  `Status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '状态',
  `FirstPhoto` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '第一张图片',
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `Seller`(`SellerID`) USING BTREE,
  INDEX `variety`(`Category`) USING BTREE,
  CONSTRAINT `CommodityCategory` FOREIGN KEY (`Category`) REFERENCES `category` (`Category`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `SellerID` FOREIGN KEY (`SellerID`) REFERENCES `userlog` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of commodityinfo
-- ----------------------------
INSERT INTO `commodityinfo` VALUES (1, '苹果', 123.00, '很红的苹果', '2023-04-05 09:04:23', 12, '河北', '宠物用品', '上货中国', '真不从');
INSERT INTO `commodityinfo` VALUES (2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for images
-- ----------------------------
DROP TABLE IF EXISTS `images`;
CREATE TABLE `images`  (
  `ID` int(0) NOT NULL AUTO_INCREMENT,
  `CommodityImage` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片',
  `CommodityID` int(0) NOT NULL COMMENT '商品ID',
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `CommodityImageID`(`CommodityID`) USING BTREE,
  CONSTRAINT `CommodityImageID` FOREIGN KEY (`CommodityID`) REFERENCES `commodityinfo` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of images
-- ----------------------------
INSERT INTO `images` VALUES (3, '23123123', 1);

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `ID` int(0) NOT NULL AUTO_INCREMENT,
  `CommodityID` int(0) NOT NULL COMMENT '商品ID',
  `UserID` int(0) NOT NULL COMMENT '买家用户ID',
  `PersonID` int(0) NOT NULL COMMENT '个人信息ID',
  `SubmitTime` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '成交时间',
  `SubmitPrices` float(8, 2) NOT NULL COMMENT '成交价格',
  `LogisticsStatus` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物流状态',
  `OrderStatus` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单状态',
  `SellUserID` int(0) NOT NULL COMMENT '卖家用户ID',
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `Commodity`(`CommodityID`) USING BTREE,
  INDEX `User`(`UserID`) USING BTREE,
  INDEX `Person`(`PersonID`) USING BTREE,
  INDEX `SellerUserID`(`SellUserID`) USING BTREE,
  CONSTRAINT `ComOrderID` FOREIGN KEY (`CommodityID`) REFERENCES `commodityinfo` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `SellerUserID` FOREIGN KEY (`SellUserID`) REFERENCES `userlog` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `UserOrderID` FOREIGN KEY (`UserID`) REFERENCES `userlog` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (1, 1, 18, 2, '2023-04-05 09:31:07', 321.00, '一大包', '12312', 12);

-- ----------------------------
-- Table structure for personinfo
-- ----------------------------
DROP TABLE IF EXISTS `personinfo`;
CREATE TABLE `personinfo`  (
  `ID` int(0) NOT NULL AUTO_INCREMENT,
  `UserID` int(0) NULL DEFAULT NULL COMMENT '用户ID',
  `Telephone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话',
  `Name` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓名',
  `Address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `UserID`(`UserID`) USING BTREE,
  CONSTRAINT `PersonID` FOREIGN KEY (`UserID`) REFERENCES `userlog` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of personinfo
-- ----------------------------
INSERT INTO `personinfo` VALUES (2, 18, '1234567', '张三', '示例地址');

-- ----------------------------
-- Table structure for sellcommodity
-- ----------------------------
DROP TABLE IF EXISTS `sellcommodity`;
CREATE TABLE `sellcommodity`  (
  `ID` int(0) NOT NULL AUTO_INCREMENT,
  `Name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拍卖商品名称',
  `FirstPrice` float(8, 0) NULL DEFAULT NULL COMMENT '起拍价格',
  `StartTime` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '发布时间',
  `EndTime` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '截止时间 ',
  `MinAddPrice` float(8, 0) NULL DEFAULT NULL COMMENT '最小加价',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sellcommodity
-- ----------------------------
INSERT INTO `sellcommodity` VALUES (1, '牙膏', 20, '2023-04-19 21:12:34', '2023-04-19 21:12:38', 2323);

-- ----------------------------
-- Table structure for userinfo
-- ----------------------------
DROP TABLE IF EXISTS `userinfo`;
CREATE TABLE `userinfo`  (
  `ID` int(0) NOT NULL AUTO_INCREMENT,
  `Nickname` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `Avatar` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `UserID` int(0) NOT NULL COMMENT '用户id\r\n',
  PRIMARY KEY (`ID`) USING BTREE,
  INDEX `UserInfoID`(`UserID`) USING BTREE,
  CONSTRAINT `UserInfoID` FOREIGN KEY (`UserID`) REFERENCES `userlog` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of userinfo
-- ----------------------------
INSERT INTO `userinfo` VALUES (6, '张三', '图片', 29);

-- ----------------------------
-- Table structure for userlog
-- ----------------------------
DROP TABLE IF EXISTS `userlog`;
CREATE TABLE `userlog`  (
  `ID` int(0) NOT NULL AUTO_INCREMENT,
  `Account` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号',
  `Password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `Authority` int(10) UNSIGNED ZEROFILL NULL DEFAULT 0000000000 COMMENT '权限',
  `Email` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE INDEX `Account`(`Account`) USING BTREE,
  INDEX `Email`(`Email`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of userlog
-- ----------------------------
INSERT INTO `userlog` VALUES (10, 'test_user_1', '220466675e31b9d20c051d5e57974150', 0000000000, 'test1@example.com');
INSERT INTO `userlog` VALUES (12, 'test_user_2', '202cb962ac59075b964b07152d234b70', 0000000000, 'test2@example.com');
INSERT INTO `userlog` VALUES (18, 'test_user_3', '202cb962ac59075b964b07152d234b70', 0000000000, 'test3@example.com');
INSERT INTO `userlog` VALUES (29, '张三', '123456', 0000000000, 'zhangsan@test.com');
INSERT INTO `userlog` VALUES (30, '李四', '123456', 0000000000, 'lisi@test.com');
INSERT INTO `userlog` VALUES (31, '王五', '123456', 0000000000, 'wangwu@test.com');
INSERT INTO `userlog` VALUES (32, '赵六', '123456', 0000000000, 'zhaoliu@test.com');
INSERT INTO `userlog` VALUES (33, '小明', '123456', 0000000000, 'xiaoming@test.com');

SET FOREIGN_KEY_CHECKS = 1;
