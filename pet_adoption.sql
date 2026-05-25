/*
 Navicat Premium Data Transfer

 Source Server         : ambow
 Source Server Type    : MySQL
 Source Server Version : 80036
 Source Host           : localhost:3306
 Source Schema         : pet_adoption

 Target Server Type    : MySQL
 Target Server Version : 80036
 File Encoding         : 65001

 Date: 07/10/2025 14:16:14
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for adoption
-- ----------------------------
DROP TABLE IF EXISTS `adoption`;
CREATE TABLE `adoption`  (
  `adoption_id` int NOT NULL AUTO_INCREMENT,
  `applicant_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `applicant_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `pet_id` int NOT NULL,
  `apply_date` datetime NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `review_date` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`adoption_id`) USING BTREE,
  INDEX `pet_id`(`pet_id`) USING BTREE,
  CONSTRAINT `adoption_ibfk_1` FOREIGN KEY (`pet_id`) REFERENCES `pet` (`pet_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of adoption
-- ----------------------------
INSERT INTO `adoption` VALUES (1, '张三', '13800138000', 3, '2025-09-08 19:52:34', '待审核', '希望尽快领养', '2025-09-08 19:52:34');
INSERT INTO `adoption` VALUES (2, '李四', '13900139000', 2, '2025-09-22 11:19:17', '已通过', '有养宠经验，符合条件', '2025-09-22 11:19:17');
INSERT INTO `adoption` VALUES (3, 'ssss', '13500135000', 12, '2025-09-22 10:46:03', '待审核', '有饲养经验，可定期反馈宠物情况', '2025-09-22 10:46:03');
INSERT INTO `adoption` VALUES (5, '十多个省份', '13142645803', 13, '2025-09-22 10:46:19', '待审核', '的规范和消费规模', '2025-09-22 10:46:19');
INSERT INTO `adoption` VALUES (6, 'Hasidic', '13142645803', 16, '2025-09-22 10:46:25', '已通过', 'Vxdvfdb', '2025-09-22 10:46:25');
INSERT INTO `adoption` VALUES (7, '士大夫', '13142645803', 17, '2025-09-22 10:46:35', '待审核', '胜多负少士大夫', '2025-09-22 10:46:35');
INSERT INTO `adoption` VALUES (8, '阿斯顿', '13142645803', 18, '2025-09-22 10:46:56', '待审核', '士大夫士大夫你哦地方', '2025-09-22 10:46:56');
INSERT INTO `adoption` VALUES (9, 'ghdf', '13142645803', 1, '2025-09-12 14:06:17', '待审核', '而感到给对方', '2025-09-12 14:06:17');
INSERT INTO `adoption` VALUES (10, '啊啊啊', '13142645803', 18, '2025-09-22 10:57:32', '待审核', '在v字典序', '2025-09-22 10:57:32');
INSERT INTO `adoption` VALUES (12, '撒萨达', '13142645803', 3, '2025-09-22 11:33:04', '待审核', 'dsdc犯得上发射点', '2025-09-22 11:33:04');
INSERT INTO `adoption` VALUES (13, '上午', '13142645803', 12, '2025-09-22 13:50:06', '待审核', '打发士大夫但是', '2025-09-22 13:50:06');
INSERT INTO `adoption` VALUES (15, 'aaa', '13142645803', 1, '2025-09-24 16:05:55', '待审核', '向法国发过', '2025-09-24 16:05:55');

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `total_amount` decimal(10, 2) NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime NOT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`order_id`) USING BTREE,
  INDEX `111`(`user_id`, `user_name`, `user_phone`) USING BTREE,
  CONSTRAINT `111` FOREIGN KEY (`user_id`, `user_name`, `user_phone`) REFERENCES `user` (`user_id`, `user_name`, `user_phone`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1040 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (1028, 31, 'liuliuliu', '13142645803', 427.90, '已支付', '2025-09-12 14:10:03', '包含2件商品: 即行猫屋 x1, 自动喂食器 x1');
INSERT INTO `order` VALUES (1032, 29, 'tom', '13142645803', 128.00, '已支付', '2025-09-15 10:55:00', '包含1件商品: 即行猫屋 x1');
INSERT INTO `order` VALUES (1039, 39, 'liuliu', '13142645803', 45.00, '已支付', '2025-09-24 16:06:50', '包含1件商品: 猫抓板 x1');

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `orderItem_id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NULL DEFAULT NULL,
  `user_id` int NOT NULL DEFAULT 1 COMMENT '所属用户ID',
  `product_id` int NOT NULL,
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `quantity` int NOT NULL,
  `price` decimal(10, 2) NOT NULL,
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态：0-购物车中，1-已下单，2-已移除',
  PRIMARY KEY (`orderItem_id`) USING BTREE,
  INDEX `order_id`(`order_id`) USING BTREE,
  INDEX `idx_order_item_user_id`(`user_id`) USING BTREE,
  INDEX `idx_order_item_status`(`status`) USING BTREE,
  INDEX `order_item_ibfk_2`(`product_id`, `product_name`) USING BTREE,
  CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `order` (`order_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `order_item_ibfk_2` FOREIGN KEY (`product_id`, `product_name`) REFERENCES `pet_supplies` (`supply_id`, `supply_name`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `order_item_ibfk_3` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 129 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (69, NULL, 31, 3, '宠物沐浴露', 1, 32.50, 0);
INSERT INTO `order_item` VALUES (101, NULL, 31, 11, '自动喂食器', 1, 299.90, 0);
INSERT INTO `order_item` VALUES (102, NULL, 31, 13, '小狗衣服', 1, 52.90, 0);
INSERT INTO `order_item` VALUES (118, NULL, 29, 11, '自动喂食器', 1, 299.90, 0);
INSERT INTO `order_item` VALUES (119, NULL, 29, 9, '旅行狗屋', 1, 168.00, 0);
INSERT INTO `order_item` VALUES (125, NULL, 29, 15, '宠物指甲剪', 1, 25.50, 0);
INSERT INTO `order_item` VALUES (127, NULL, 39, 1, '即行猫屋', 1, 128.00, 0);

-- ----------------------------
-- Table structure for pet
-- ----------------------------
DROP TABLE IF EXISTS `pet`;
CREATE TABLE `pet`  (
  `pet_id` int NOT NULL AUTO_INCREMENT,
  `pet_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `pet_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `pet_gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `pet_age` int NOT NULL,
  `pet_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `pet_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `pet_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_date` datetime NOT NULL,
  PRIMARY KEY (`pet_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pet
-- ----------------------------
INSERT INTO `pet` VALUES (1, '拉不拉的多拉不拉', '犬类', '公', 3, '已领养', '性格活泼，已完成疫苗接种', 'http://localhost:8080/springboot_adoption/images/ec7a23d5-23d7-445a-866a-44c135740b41.png', '2024-01-01 00:00:00');
INSERT INTO `pet` VALUES (2, '灰不溜秋', '猫类', '母', 2, '已领养', '活泼可爱，爱干净', 'http://localhost:8080/springboot_adoption/images/5bfe7527-fa64-4078-b1ff-f35df1bdbbfb.png', '2024-03-15 00:00:00');
INSERT INTO `pet` VALUES (3, '小花', '猫类', '母', 1, '已领养', '活泼可爱，会用猫砂', 'http://localhost:8080/springboot_adoption/images/001e57f2-83e1-4ee6-b429-9d0c70960b65.png', '2025-09-02 16:49:00');
INSERT INTO `pet` VALUES (12, '大聪明', '犬类', '公', 2, '可领养', '聪明的小边牧', 'http://localhost:8080/springboot_adoption/images/cbd65a66-d5eb-473c-928a-95cd741a49f9.png', '2025-09-06 22:12:26');
INSERT INTO `pet` VALUES (13, '粘人小暹罗', '猫类', '公', 3, '可领养', '可粘人了，爱讲卫生', 'http://localhost:8080/springboot_adoption/images/59159d91-335c-4273-b1f4-dfa348ccf6fb.png', '2025-09-06 22:12:49');
INSERT INTO `pet` VALUES (16, '萨摩耶', '犬类', '公', 34, '可领养', '雪白雪白的', 'http://localhost:8080/springboot_adoption/images/a4608c3c-1d31-4021-ae0e-923a604040ad.png', '2025-09-08 10:36:39');
INSERT INTO `pet` VALUES (17, '小黑', '猫类', '公', 2, '可领养', '超级黑', 'http://localhost:8080/springboot_adoption/images/1cb1bd0d-9ed2-4196-8183-f3cb916a044f.png', '2025-09-08 10:55:38');
INSERT INTO `pet` VALUES (18, '短腿柯基', '犬类', '公', 2, '寻主中', '腿很短', 'http://localhost:8080/springboot_adoption/images/5aff5541-4297-47f7-be8e-5424ea35d8b1.png', '2025-09-08 15:27:38');
INSERT INTO `pet` VALUES (21, '橘座大人', '猫类', '公', 3, '可领养', '体型圆润，性格温顺，爱吃小鱼干', 'http://localhost:8080/springboot_adoption/images/3862d2b9-8a76-45ae-9006-a8ee46c4c4fe.png', '2025-09-10 09:30:00');
INSERT INTO `pet` VALUES (22, '边牧咖啡', '犬类', '母', 1, '可领养', '聪明活泼，已进行基础训练', 'http://localhost:8080/springboot_adoption/images/22283977-9002-47bf-b9c6-def5ad292d82.png', '2025-09-12 14:15:00');
INSERT INTO `pet` VALUES (23, '三花妹妹', '猫类', '母', 2, '寻主中', '三花猫，亲人粘人，已绝育', 'http://localhost:8080/springboot_adoption/images/eb41ad99-fcaf-4dc9-a18c-0abfeaf9033c.png', '2025-09-15 11:20:00');
INSERT INTO `pet` VALUES (24, '金毛暖暖', '犬类', '公', 4, '可领养', '温顺亲人，适合有孩子的家庭', 'http://localhost:8080/springboot_adoption/images/f0499529-9d1d-457b-903f-5ce0e7dc96dc.png', '2025-09-16 16:40:00');
INSERT INTO `pet` VALUES (25, '布偶雪球', '猫类', '母', 1, '可领养', '长毛蓝眼睛，性格安静', 'http://localhost:8080/springboot_adoption/images/b760b9f5-3c4a-4f43-9f8f-290b0c848309.png', '2025-09-18 10:05:00');
INSERT INTO `pet` VALUES (26, '泰迪豆豆', '犬类', '公', 2, '已领养', '棕色卷毛，体型小巧', 'http://localhost:8080/springboot_adoption/images/e4985da9-9d43-47bb-bd53-38d653f6d978.png', '2025-09-19 08:30:00');
INSERT INTO `pet` VALUES (27, '美短灰灰', '猫类', '公', 3, '可领养', '美国短毛猫，会使用猫抓板', 'http://localhost:8080/springboot_adoption/images/4052c436-7454-49e5-9988-3d3aad6f21d8.png', '2025-09-20 15:25:00');
INSERT INTO `pet` VALUES (28, '哈士奇哈哈', '犬类', '公', 2, '寻主中', '精力充沛，有点拆家天赋', 'http://localhost:8080/springboot_adoption/images/b26e8a94-fa90-40cb-b04a-c3b9581bff7b.png', '2025-09-21 13:40:00');
INSERT INTO `pet` VALUES (29, '英短蓝猫', '猫类', '母', 2, '可领养', '蓝色短毛，性格独立', 'http://localhost:8080/springboot_adoption/images/921e211f-a964-45f0-9972-c3641ab34c10.png', '2025-09-22 10:15:00');

-- ----------------------------
-- Table structure for pet_boarding
-- ----------------------------
DROP TABLE IF EXISTS `pet_boarding`;
CREATE TABLE `pet_boarding`  (
  `boarding_id` int NOT NULL AUTO_INCREMENT,
  `user_pet_id` int NOT NULL,
  `user_id` int NOT NULL,
  `boarding_start` datetime NOT NULL,
  `boarding_end` datetime NOT NULL,
  `boarding_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '寄养中',
  `fee` decimal(10, 2) NOT NULL,
  `payment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '未支付',
  `special_instructions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `pet_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`boarding_id`) USING BTREE,
  INDEX `user_pet_id`(`user_pet_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `pet_boarding_ibfk_1` FOREIGN KEY (`user_pet_id`) REFERENCES `userpet_boarding` (`user_pet_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `pet_boarding_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pet_boarding
-- ----------------------------
INSERT INTO `pet_boarding` VALUES (1, 6, 2, '2025-09-15 09:00:00', '2025-09-20 18:00:00', '寄养中', 300.00, '已支付', '每天需要遛两次', '2025-09-11 09:36:12', 'http://localhost:8080/springboot_adoption/images/ec7a23d5-23d7-445a-866a-44c135740b41.png');
INSERT INTO `pet_boarding` VALUES (2, 7, 3, '2025-09-20 10:00:00', '2025-09-25 17:00:00', '待寄养', 200.00, '未支付', '食物需分餐喂食', '2025-09-11 09:36:12', 'http://localhost:8080/springboot_adoption/images/ec7a23d5-23d7-445a-866a-44c135740b41.png');
INSERT INTO `pet_boarding` VALUES (3, 8, 29, '2025-09-12 08:00:00', '2025-09-18 19:00:00', '寄养中', 450.00, '已支付', '需要按时喂药', '2025-09-11 09:36:12', 'http://localhost:8080/springboot_adoption/images/ec7a23d5-23d7-445a-866a-44c135740b41.png');
INSERT INTO `pet_boarding` VALUES (4, 9, 16, '2025-09-22 09:30:00', '2025-09-28 16:00:00', '待寄养', 250.00, '未支付', '不要和其他宠物混养', '2025-09-11 09:36:12', 'http://localhost:8080/springboot_adoption/images/ec7a23d5-23d7-445a-866a-44c135740b41.png');
INSERT INTO `pet_boarding` VALUES (26, 7, 29, '2024-05-01 00:00:00', '2024-05-10 00:00:00', '寄养中', 500.00, '未支付', '每天需要遛弯两次，不吃辛辣食物', '2025-09-11 21:13:09', 'https://example.com/pets/dog123.jpg');
INSERT INTO `pet_boarding` VALUES (27, 16, 29, '2024-05-10 00:00:00', '2024-05-20 00:00:00', '寄养中', 300.50, '已支付', '每日喂食两次，早晚各一次，需要遛弯', '2025-09-11 21:28:20', 'https://example.com/pets/dog123.jpg');
INSERT INTO `pet_boarding` VALUES (36, 74, 31, '2025-09-23 00:00:00', '2025-09-25 00:00:00', '已付款', 120.00, '已支付', '啊大苏打撒旦', '2025-09-23 14:49:20', 'http://localhost:8080/springboot_adoption/images/416bd1b8-36b9-448e-a887-fe69c27a51c4.png');
INSERT INTO `pet_boarding` VALUES (37, 75, 39, '2025-09-24 00:00:00', '2025-09-26 00:00:00', '已付款', 180.00, '已支付', '的发电公司地方', '2025-09-24 16:11:47', 'http://localhost:8080/springboot_adoption/images/a3b6c469-c0d2-46e7-8fa3-85d10d5f9eae.png');

-- ----------------------------
-- Table structure for pet_report
-- ----------------------------
DROP TABLE IF EXISTS `pet_report`;
CREATE TABLE `pet_report`  (
  `report_id` int NOT NULL AUTO_INCREMENT,
  `pet_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `pet_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `pet_gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `pet_age` int NULL DEFAULT NULL,
  `find_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `contact_info` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `report_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `report_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_date` datetime NOT NULL,
  PRIMARY KEY (`report_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pet_report
-- ----------------------------
INSERT INTO `pet_report` VALUES (1, '流浪小花', '猫类', '母', 1, '八一公园东门', '13800138000', '在公园东门发现一只流浪猫，比较亲人，身上有轻微外伤', 'http://localhost:8080/springboot_adoption/images/ded78265-4ce5-4ffa-9b51-013d92479d62.png', '2025-09-12 22:56:48');
INSERT INTO `pet_report` VALUES (2, '流浪大黄', '犬类', '公', 2, '喜瑞都小区门口', '13900139000', '在小区门口看到一只流浪狗，看起来比较虚弱，需要帮助', 'http://localhost:8080/springboot_adoption/images/6f575bc3-2170-43e0-9dfb-1b78c65ba51f.png', '2025-09-12 22:57:05');
INSERT INTO `pet_report` VALUES (3, '雪球', '猫', '公', 2, '幸福小区3号楼附近', '13800138000', '白色英短，脖子上有蓝色项圈，于2024-09-01走失', 'http://localhost:8080/springboot_adoption/images/af8d7c5b-3354-4228-8338-11f65658fea5.png', '2025-09-12 22:57:30');
INSERT INTO `pet_report` VALUES (7, '流浪边牧', '犬类', '公', 3, '创新谷', '13142645803', '爱吃六楼烤肠', 'http://localhost:8080/springboot_adoption/images/05aa4933-c0fb-4226-b9f9-e05e70a879d1.png', '2025-09-12 23:02:06');
INSERT INTO `pet_report` VALUES (8, '垃圾桶旁狸花', '猫类', '公', 3, '来福新剧', '13142645803', '可可怜怜没饭吃', 'http://localhost:8080/springboot_adoption/images/b7c57e57-fb72-4d0e-bf91-6c330123d57a.png', '2025-09-12 23:03:17');
INSERT INTO `pet_report` VALUES (9, '纸箱小橘', '猫类', '母', 2, '一堆纸箱旁边', '13142645803', '瘦瘦小小的 可可怜怜的', 'http://localhost:8080/springboot_adoption/images/5eb28cff-5c00-460b-804c-1b4b26e6c93f.png', '2025-09-12 23:24:13');
INSERT INTO `pet_report` VALUES (10, '走失泰迪', '犬类', '母', 1, '阳光花园2期南门', '13700137000', '棕色泰迪，名字叫“布丁”，9月22日傍晚走失，脖子系红色蝴蝶结项圈', 'http://localhost:8080/springboot_adoption/images/32a31b2c-17e7-42ba-81c9-e679524a7bd3.png', '2025-09-22 14:54:31');
INSERT INTO `pet_report` VALUES (11, '路边美短', '猫类', '公', 2, '建设路与文化路交叉口', '13600136000', '发现一只受伤美短，右前爪有伤口，已临时安置在附近宠物医院', 'http://localhost:8080/springboot_adoption/images/b1074e14-33a9-4f09-9d4e-b5e14824fe14.png', '2025-09-22 14:54:38');

-- ----------------------------
-- Table structure for pet_supplies
-- ----------------------------
DROP TABLE IF EXISTS `pet_supplies`;
CREATE TABLE `pet_supplies`  (
  `supply_id` int NOT NULL AUTO_INCREMENT,
  `supply_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `supply_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `brand` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `price` decimal(10, 2) NOT NULL,
  `stock` int NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `supply_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_date` datetime NOT NULL,
  PRIMARY KEY (`supply_id`) USING BTREE,
  INDEX `supply_id`(`supply_id`, `supply_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pet_supplies
-- ----------------------------
INSERT INTO `pet_supplies` VALUES (1, '即行猫屋', '玩具互动', '麦富迪', 128.00, 200, '上架', '承重至约 60 磅，便携收纳；出行、就医、搬家更省心', 'http://localhost:8080/springboot_adoption/images/77083cee-80bf-44fb-814b-6eaf03f5c73e.png', '2025-09-08 17:17:34');
INSERT INTO `pet_supplies` VALUES (2, '猫抓板', '玩具互动', '未卡', 45.00, 150, '上架', '高密度瓦楞纸材质，耐用不易掉屑', 'http://localhost:8080/springboot_adoption/images/ba9cbe1c-4b65-4575-a613-872fe6cd3c9b.png', '2025-09-08 17:17:57');
INSERT INTO `pet_supplies` VALUES (3, '宠物沐浴露', '洗护用品', '狮王', 32.50, 80, '上架', '温和配方，深层清洁不刺激皮肤', 'http://localhost:8080/springboot_adoption/images/4b2bdd22-9ba9-495e-a995-bc3b2645c7ef.png', '2025-09-22 10:19:08');
INSERT INTO `pet_supplies` VALUES (8, '可爱狗狗盆', '窝垫家具', 'dsd', 59.90, 0, '上架', '大容量，狗狗吃的饱', 'http://localhost:8080/springboot_adoption/images/1547c0b3-be8d-48b6-8bd1-e53dccf987d7.png', '2025-09-08 17:19:33');
INSERT INTO `pet_supplies` VALUES (9, '旅行狗屋', '出行装备', '阿里巴巴', 168.00, 8, '上架', '承重至约 60 磅，便携收纳；出行、就医、搬家更省心', 'http://localhost:8080/springboot_adoption/images/18a9cd8d-12c7-465f-bac7-e0a75bb86a03.png', '2025-09-08 17:29:48');
INSERT INTO `pet_supplies` VALUES (10, '牵引绳', '出行装备', '卡弗斯', 56.90, 3, '上架', '全衬垫舒适设计，有效抑制爆冲；调节方便，日常 / 训练皆宜', 'http://localhost:8080/springboot_adoption/images/90e0bdda-8bb5-4245-995b-8db6a2ff991e.png', '2025-09-08 17:32:57');
INSERT INTO `pet_supplies` VALUES (11, '自动喂食器', '窝垫家具', '卢卡斯', 299.90, 5, '上架', '便携定时定量，支持干粮 / 冻干混合；不锈钢碗盘易清洁；断电续航与防卡粮设计', 'http://localhost:8080/springboot_adoption/images/313ea311-51ff-4b5f-9f4b-70373ebbcc3e.png', '2025-09-08 17:33:55');
INSERT INTO `pet_supplies` VALUES (12, '温暖窝窝', '窝垫家具', '宠物用品集结点', 55.90, 9, '上架', '全衬垫舒适设计', 'http://localhost:8080/springboot_adoption/images/1ab16d82-2916-443f-8eba-baf06b53e37e.png', '2025-09-08 17:35:17');
INSERT INTO `pet_supplies` VALUES (13, '小狗衣服', '窝垫家具', '旺财', 52.90, 5, '上架', '可可爱爱，舒适柔软', 'http://localhost:8080/springboot_adoption/images/d6684fdb-aa3b-4089-b60a-f8c8e9401f56.png', '2025-09-08 17:36:04');
INSERT INTO `pet_supplies` VALUES (14, '抗菌宠物窝', '窝垫家具', '宠舒宝', 89.90, 30, '上架', '抑菌防螨面料，四季通用，适合中小型犬猫', 'http://localhost:8080/springboot_adoption/images/42dbc527-8629-4f1d-b07c-174cda45453d.png', '2025-09-22 10:40:03');
INSERT INTO `pet_supplies` VALUES (15, '宠物指甲剪', '洗护用品', '派锐', 25.50, 120, '上架', '安全圆头设计，防止过度修剪，带锉刀', 'http://localhost:8080/springboot_adoption/images/b4955633-a550-4a0f-a033-d875cd3615d7.png', '2025-09-22 10:40:12');
INSERT INTO `pet_supplies` VALUES (16, '无谷狗粮', '食品零食', '渴望', 189.00, 45, '上架', '无谷物配方，含三文鱼和鸡肉，适合敏感肠胃', 'http://localhost:8080/springboot_adoption/images/1cb7c1b2-f06b-4790-83e7-73df06bc7192.png', '2025-09-22 10:40:36');
INSERT INTO `pet_supplies` VALUES (17, '宠物拾便袋', '出行装备', '宠百思', 12.90, 300, '上架', '可降解材料，便携装，带香味掩盖异味', 'http://localhost:8080/springboot_adoption/images/ffaf15d1-ee05-4206-91f2-06832b10a3b1.png', '2025-09-22 10:40:45');
INSERT INTO `pet_supplies` VALUES (18, '智能宠物项圈', '玩具互动', '小佩', 299.00, 15, '上架', '实时定位，健康监测，防水设计', 'http://localhost:8080/springboot_adoption/images/bbc1a3f0-f8b9-4669-8966-3c781bc65fdd.png', '2025-09-22 10:40:54');
INSERT INTO `pet_supplies` VALUES (19, '猫用化毛膏', '食品零食', '卫仕', 45.00, 60, '上架', '帮助排出毛球，补充营养，改善肠胃', 'http://localhost:8080/springboot_adoption/images/186d380d-0b20-4208-8691-32ebb9f0c5de.png', '2025-09-22 10:41:15');
INSERT INTO `pet_supplies` VALUES (20, '宠物饮水机', '窝垫家具', '霍曼', 129.00, 25, '上架', '自动循环过滤，静音设计，鼓励多喝水', 'http://localhost:8080/springboot_adoption/images/745fee9b-f01e-4df3-a909-3c54f374897c.png', '2025-09-22 10:41:25');

-- ----------------------------
-- Table structure for public_information
-- ----------------------------
DROP TABLE IF EXISTS `public_information`;
CREATE TABLE `public_information`  (
  `public_id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `publish_time` datetime NULL DEFAULT NULL,
  `create_date` datetime NOT NULL,
  `public_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`public_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of public_information
-- ----------------------------
INSERT INTO `public_information` VALUES (1, '宠物领养活动通知', '本周六将举办大型宠物领养活动，欢迎各位爱心人士前来参与，现场有50+待领养宠物等待新家', '管理员', '已发布', '2024-05-10 09:30:00', '2025-09-09 23:25:18', 'http://localhost:8080/springboot_adoption/images/edc37246-b14d-4998-ab82-cb7e0f65663d.png');
INSERT INTO `public_information` VALUES (2, '志愿者招募公告', '因近期救助任务增多，现招募10名志愿者协助日常宠物照料工作，有意者请联系客服', '运营部', '已发布', '2024-05-15 14:20:00', '2025-09-09 19:54:22', 'http://localhost:8080/springboot_adoption/images/e0386dd7-a273-4ed9-8b31-3bae32cfb5f7.png');
INSERT INTO `public_information` VALUES (3, '系统升级维护通知', '预计6月1日进行系统升级，期间可能无法正常访问，敬请谅解', '技术部', '已发布', '2025-09-05 23:53:48', '2025-09-09 23:30:15', 'http://localhost:8080/springboot_adoption/images/78a49e0a-2355-4fbf-bc19-833c5ffacd15.png');
INSERT INTO `public_information` VALUES (8, '宠物基础医疗知识讲座', '为帮助领养人更好地照顾宠物，本周末将举办宠物基础医疗知识讲座，内容包括常见疾病预防、日常护理等，欢迎所有领养人参加。', '宠物健康部', '已发布', '2023-10-28 09:00:00', '2025-09-09 23:30:25', 'http://localhost:8080/springboot_adoption/images/fec49ad4-c558-4768-8d14-8ebed24bd1c4.png');
INSERT INTO `public_information` VALUES (9, '冬季宠物领养暖心活动', '12月期间领养成年宠物可享受宠物用品大礼包一份，包含粮食、玩具和冬季保暖用品，数量有限先到先得。', '活动策划部', '已发布', '2023-11-30 10:00:00', '2025-09-09 23:30:32', 'http://localhost:8080/springboot_adoption/images/db77a2ff-4cb9-438a-9384-56c9720cb1d5.png');
INSERT INTO `public_information` VALUES (10, '新救助一批流浪宠物待领养', '本周新救助10只流浪宠物，已完成健康检查和疫苗接种，包括3只幼犬、2只成年犬和5只猫咪，欢迎爱心人士前来领养。', '救助中心', '已发布', '2023-12-05 15:20:00', '2025-09-09 23:31:30', 'http://localhost:8080/springboot_adoption/images/4ddb9273-cc30-4ae3-b065-0c80d9e61266.png');
INSERT INTO `public_information` VALUES (11, '2024年度志愿者培训计划', '拟定于2024年1月开展志愿者培训，内容包括宠物照料、沟通技巧、活动组织等，具体时间将另行通知。', '志愿者管理部', '已发布', '2025-09-09 22:09:19', '2025-09-09 23:32:37', 'http://localhost:8080/springboot_adoption/images/1197e835-1ad6-40d2-ae87-03d4251b8865.png');
INSERT INTO `public_information` VALUES (12, '春节期间领养中心营业时间调整', '春节期间（2月10日-2月17日）领养中心营业时间调整为10:00-15:00，2月18日恢复正常营业时间，敬请留意。', '行政部', '已发布', '2024-01-20 08:30:00', '2025-09-09 23:31:38', 'http://localhost:8080/springboot_adoption/images/86fe13ab-979c-4e91-825b-dd690f73e435.png');

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `permissions` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_date` datetime NOT NULL,
  PRIMARY KEY (`role_id`) USING BTREE,
  UNIQUE INDEX `role_code`(`role_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, '管理员', 'admin', '用户管理，角色管理，订单管理，', '系统管理员，拥有所有权限', '2024-01-01 00:00:00');
INSERT INTO `role` VALUES (2, '普通用户', 'user', '物品管理', '普通用户，仅拥有领养申请权限', '2024-01-10 00:00:00');
INSERT INTO `role` VALUES (3, '志愿者', 'volunteer', '宠物管理', '志愿者，负责宠物管理和领养审核', '2024-02-15 00:00:00');
INSERT INTO `role` VALUES (6, '审核员', 'select', '宠物管理,角色管理,领养审核', 'sfsfd', '2025-09-05 22:54:03');

-- ----------------------------
-- Table structure for t_place
-- ----------------------------
DROP TABLE IF EXISTS `t_place`;
CREATE TABLE `t_place`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '地点名称',
  `address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详细地址',
  `longitude` double NOT NULL COMMENT '经度',
  `latitude` double NOT NULL COMMENT '纬度',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '地图地点信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_place
-- ----------------------------
INSERT INTO `t_place` VALUES (2, '北京市朝阳区来福乐猫犬舍', '北京市朝阳区来福乐猫犬舍', 116.457865, 39.84133, '2025-09-24 14:00:36');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_age` int NULL DEFAULT NULL,
  `user_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_date` datetime NOT NULL,
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `money` decimal(10, 0) UNSIGNED ZEROFILL NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE,
  INDEX `user_id`(`user_id`, `user_name`, `user_phone`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (2, '张三', 'zhangsan123', '男', 30, '13987654321', 'zhangsan@example.com', 'admin', '2024-01-15 00:00:00', NULL, 0000000650);
INSERT INTO `user` VALUES (3, '李四', 'lisi456', '女', 24, '13798765432', 'lisi@example.com', 'user', '2024-03-20 00:00:00', NULL, 0000000600);
INSERT INTO `user` VALUES (4, 'wangwu', 'wangwu789', '男', 35, '13654321876', 'wangwu@example.com', 'user', '2024-02-28 00:00:00', NULL, 0000000600);
INSERT INTO `user` VALUES (16, 'b', '123', '女', 34, '13142645803', '3609424196@qq.com', 'admin', '2025-09-02 12:09:09', NULL, 0000000600);
INSERT INTO `user` VALUES (17, '嘎嘎嘎', '123', 'male', 34, '13142645803', '3609424196@qq.com', 'admin', '2025-09-05 09:02:42', 'http://localhost:8080/springboot_adoption/images/b2866d27-469c-4879-954e-96f04c94ab2b.jpg', 0000000600);
INSERT INTO `user` VALUES (18, '啦啦啦', '$2a$10$PpCZmxObllwq/XNPKGFVB.b64fho73ATtmgslwqZh1Tc4fFF0rQY6', 'male', 52, '13142645803', '3609424196@qq.com', 'volunteer', '2025-09-05 10:35:24', 'http://localhost:8080/springboot_adoption/images/220b7fe4-1be0-4c3a-8eb4-4938c6cd6a18.jpg', 0000000600);
INSERT INTO `user` VALUES (19, '啊大大v', '$2a$10$DH3BmR4xoRvutBlj6mZwCuZjg/PF0hJTdiU0wUBpWV1J4/dKcjAU6', 'male', 12, '13142645803', '3609424196@qq.com', 'user', '2025-09-05 11:05:22', 'http://localhost:8080/springboot_adoption/images/496877e1-1ea1-4f65-9038-178773d8de70.jpg', 0000000600);
INSERT INTO `user` VALUES (21, '刘欣', '$2a$10$LdonX9GqrzIDyYHfMf4SN.TMgGPP88e3qAKrGpHQkcKuvKUACDNVG', 'female', 22, '13142645803', '3609424196@qq.com', 'user', '2025-09-08 15:53:17', 'http://localhost:8080/springboot_adoption/images/a9cf7f5a-340a-4c5a-9e4d-4b95adbe1a64.jpg', 0000000600);
INSERT INTO `user` VALUES (22, '李', '$2a$10$f3S5zOlO6sZyY1w/mlLWievKAAxyUJKxwl773a3VQWEykagyZcKH2', 'male', 8, '13142645803', '3609424196@qq.com', 'user', '2025-09-08 19:01:31', 'http://localhost:8080/springboot_adoption/images/9eb4f939-292e-4f2d-bca4-b885429c3f45.jpg', 0000000600);
INSERT INTO `user` VALUES (24, 'aaa', '$2a$10$LB8fOEiCqKr8W6nIT/vS4OW2t/GIXJU1fGcDRBHJ155UEuolEuxk2', 'male', 22, '15242545256', '3609424196@qq.com', 'user', '2025-09-09 17:31:50', 'http://localhost:8080/springboot_adoption/images/7ffb471a-493e-4ac9-a657-0d610cbe069e.png', 0000000600);
INSERT INTO `user` VALUES (28, 'bbb', '$2a$10$RwaicOGPJcQjLwnCm/dHCeA.SPHYw70USnylspNHgPnPUXlW7kPaS', 'male', 58, '13142645803', '3609424196@qq.com', 'user', '2025-09-09 17:58:50', 'http://localhost:8080/springboot_adoption/images/ea11fe9c-e8a6-4d8d-8372-ed52e800c66a.png', 0000000600);
INSERT INTO `user` VALUES (29, 'tom', '$2a$10$Q4JpmpnyZPH1NRSU/eXEX.rxBeP2j8erBMLHTgigK3VbrQO2jgCoG', 'male', 55, '13142645803', '3609424196@qq.com', 'admin', '2025-09-09 19:21:42', 'http://localhost:8080/springboot_adoption/images/bbdf8ab9-f257-4d72-bac3-105554cffd16.png', 0000000157);
INSERT INTO `user` VALUES (31, 'liuliuliu', '$2a$10$3ZYOxx/RxlacV9s9Yg1jbewcdtoGUu/CHmnOllGr15rC9917ZtxIu', 'male', 25, '13142645803', '3609424196@qq.com', 'user', '2025-09-10 14:26:54', 'http://localhost:8080/springboot_adoption/images/9ac6d0ce-7aee-4261-9315-5b37090b7180.png', 0000000191);
INSERT INTO `user` VALUES (32, 'xiao', '$2a$10$XIWXdpi27K3f5FsM1aaJGufmKrsZOY3et3/UhL52mDGkOPCw8gFQG', 'female', 48, '13142645803', '3609424196@qq.com', 'volunteer', '2025-09-12 14:12:21', 'http://localhost:8080/springboot_adoption/images/bfd9d9a3-2f87-40fe-9e9f-94206af9bc1e.png', 0000000300);
INSERT INTO `user` VALUES (33, 'gtgt', '$2a$10$3ZYOxx/RxlacV9s9Yg1jbewcdtoGUu/CHmnOllGr15rC9917ZtxIu', 'male', 3, '13142645803', '3609424196@qq.com', 'volunteer', '2025-09-12 23:31:20', '', 0000000300);
INSERT INTO `user` VALUES (39, 'liuliu', '$2a$10$K3L7/QPkncmsSgmqu9cKpule8GPR2314q/xhTQcxBGNylUgpdNonW', 'male', 2, '13142645803', '3609424196@qq.com', 'volunteer', '2025-09-24 16:03:46', 'http://localhost:8080/springboot_adoption/images/67477e63-91fb-403a-a1b2-a2a9a7d2833a.png', 0000000375);

-- ----------------------------
-- Table structure for userpet_boarding
-- ----------------------------
DROP TABLE IF EXISTS `userpet_boarding`;
CREATE TABLE `userpet_boarding`  (
  `user_pet_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `pet_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `pet_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `pet_age` int NULL DEFAULT NULL,
  `pet_gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `health_status` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `vaccine_status` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `special_habit` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `pet_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `pet_size` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`user_pet_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `userpet_boarding_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 76 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of userpet_boarding
-- ----------------------------
INSERT INTO `userpet_boarding` VALUES (6, 2, '贝贝', '犬类', 2, '公', '健康', '已完成三联疫苗', '喜欢玩球', 'http://localhost:8080/springboot_adoption/images/1af20431-5ee9-48a8-8292-82912a676a10.png', '2025-09-11 09:30:21', '大型');
INSERT INTO `userpet_boarding` VALUES (7, 3, '咪咪', '猫类', 1, '母', '健康', '已完成猫三联', '怕生人', 'http://localhost:8080/springboot_adoption/images/45452fd7-5f07-488a-bcda-d6a0738a9e38.png', '2025-09-11 09:30:21', '大型');
INSERT INTO `userpet_boarding` VALUES (8, 29, '旺财', '犬类', 3, '公', '健康', '已完成年度疫苗', '会握手', 'http://localhost:8080/springboot_adoption/images/d4bdfe5c-c2b2-49b8-bfb8-a7a08583a6c3.png', '2025-09-11 09:30:21', '大型');
INSERT INTO `userpet_boarding` VALUES (9, 16, '雪球', '猫类', 2, '母', '健康', '已完成基础疫苗', '喜欢睡懒觉', 'http://localhost:8080/springboot_adoption/images/56d0e17c-a086-4d86-b3e1-fd2db444be08.png', '2025-09-11 09:30:21', '大型');
INSERT INTO `userpet_boarding` VALUES (16, 29, '小鹦鹉', '其他', 8, '母', '健康', '已接种', '后i后', 'http://localhost:8080/springboot_adoption/images/ac3f59e4-1c34-4680-b42e-a5689d78832e.png', '2025-09-11 15:30:07', '小型');
INSERT INTO `userpet_boarding` VALUES (74, 31, '顶顶顶', '其他', 2, '公', '健康', '已接种', '收费的方式', 'http://localhost:8080/springboot_adoption/images/416bd1b8-36b9-448e-a887-fe69c27a51c4.png', '2025-09-23 14:49:04', '小型');
INSERT INTO `userpet_boarding` VALUES (75, 39, 'aa', '犬类', 2, '公', '健康', '已接种', '打发士大夫士大夫', 'http://localhost:8080/springboot_adoption/images/a3b6c469-c0d2-46e7-8fa3-85d10d5f9eae.png', '2025-09-24 16:11:22', '中型');

-- ----------------------------
-- Table structure for volunteer
-- ----------------------------
DROP TABLE IF EXISTS `volunteer`;
CREATE TABLE `volunteer`  (
  `volunteer_id` int NOT NULL AUTO_INCREMENT COMMENT '志愿者唯一ID（主键）',
  `user_id` int NOT NULL COMMENT '关联用户表的用户ID（外键），确保志愿者账号与系统用户绑定',
  `volunteer_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '志愿者服务类型：如“宠物照料”“领养审核”“活动协助”“医疗辅助”',
  `service_hours` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '累计服务时长（小时），用于志愿者考核与激励',
  `volunteer_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '待认证' COMMENT '志愿者状态：待认证/已激活/已冻结/已离职',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '志愿者注册时间（默认当前时间）',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注：如特殊技能（如兽医背景、驯犬经验）、服务限制等',
  `volunteer_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '志愿者头像',
  PRIMARY KEY (`volunteer_id`) USING BTREE,
  INDEX `idx_volunteer_user_id`(`user_id`) USING BTREE,
  INDEX `idx_volunteer_type_status`(`volunteer_type`, `volunteer_status`) USING BTREE,
  CONSTRAINT `volunteer_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '志愿者信息表：存储系统志愿者的专属业务信息，关联用户表实现身份统一管理' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of volunteer
-- ----------------------------
INSERT INTO `volunteer` VALUES (1, 32, '宠物照料,活动协助', 120.50, '已激活', '2025-09-17 10:58:15', '有3年宠物照料经验，擅长大型犬护理', 'http://localhost:8080/springboot_adoption/images/b30e1da3-08e8-4bc1-9a22-f9a1fbd8d139.png');
INSERT INTO `volunteer` VALUES (2, 18, '活动协助,领养审核,宠物照料', 22.00, '已激活', '2025-09-17 10:58:15', '有驾驶执照，可协助流浪宠物运输', 'http://localhost:8080/springboot_adoption/images/d14504a5-2b74-4589-a74a-42d3b782b4fb.png');
INSERT INTO `volunteer` VALUES (8, 18, '领养审核,医疗辅助,流浪宠物救助', 0.00, '待审核', '2025-09-17 19:53:42', '呵呵光棍节快乐接到数十个', 'http://localhost:8080/springboot_adoption/images/cc86925f-7b58-4ff7-b549-32e09752f0fe.png');
INSERT INTO `volunteer` VALUES (9, 31, '流浪宠物救助,医疗辅助,活动协助,领养审核,宠物照料', 200.00, '待审核', '2025-09-18 09:15:34', '合理估计不会变成歌词u哦iu偶极矩你', 'http://localhost:8080/springboot_adoption/images/7c19fc8d-f282-4994-b347-f1d90eba9f23.png');
INSERT INTO `volunteer` VALUES (13, 39, '宠物照料,领养审核,活动协助', 0.00, '已激活', '2025-09-24 16:08:32', '的日日日日日日', 'http://localhost:8080/springboot_adoption/images/f7b1142c-3fdf-43c3-ad10-f83e93b56b6e.png');

SET FOREIGN_KEY_CHECKS = 1;
