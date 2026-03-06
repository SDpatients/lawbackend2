-- 债权人会议投票项表和视频标签表
USE law;

SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT;
SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS;
SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION;
SET NAMES utf8mb4;
SET @OLD_TIME_ZONE=@@TIME_ZONE;
SET TIME_ZONE='+00:00';
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO';
SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0;

-- 投票项表
DROP TABLE IF EXISTS `tb_meeting_vote_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_meeting_vote_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `meeting_id` bigint NOT NULL COMMENT '会议ID，关联会议表',
  `item_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '投票项名称',
  `agree_count` int DEFAULT 0 COMMENT '同意票数',
  `oppose_count` int DEFAULT 0 COMMENT '反对票数',
  `abstain_count` int DEFAULT 0 COMMENT '弃权票数',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-激活, INACTIVE-停用, DELETED-删除',
  PRIMARY KEY (`id`),
  KEY `idx_meeting_id` (`meeting_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='债权人会议投票项表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- 视频标签表
DROP TABLE IF EXISTS `tb_meeting_video_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_meeting_video_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `meeting_id` bigint NOT NULL COMMENT '会议ID，关联会议表',
  `video_title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '视频标题',
  `status` varchar(20) COLLATE utf8mb4_bin DEFAULT 'pending' COMMENT '生成状态：generated=已生成，pending=待生成',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_user_id` bigint DEFAULT NULL COMMENT '修改者ID',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除: 0-否, 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_meeting_id` (`meeting_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='债权人会议视频标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- 插入模拟数据
INSERT INTO `tb_meeting_vote_item` (`meeting_id`, `item_name`, `agree_count`, `oppose_count`, `abstain_count`, `remark`, `status`) VALUES
(1, '通过财产变价方案', 45, 8, 3, '已通过', 'ACTIVE'),
(1, '通过财产分配方案', 42, 10, 4, '已通过', 'ACTIVE'),
(1, '选举债权人委员会成员', 38, 12, 6, '待表决', 'ACTIVE'),
(1, '确认管理人工作报告', 50, 5, 1, '已通过', 'ACTIVE'),
(1, '审议破产财产管理方案', 44, 9, 3, '已通过', 'ACTIVE');

INSERT INTO `tb_meeting_video_tag` (`meeting_id`, `video_title`, `status`) VALUES
(1, '会议开场致辞', 'generated'),
(1, '管理人工作报告', 'generated'),
(1, '财产变价方案说明', 'pending'),
(1, '债权人提问环节', 'generated'),
(1, '投票表决过程', 'pending'),
(1, '会议总结发言', 'generated');

SET TIME_ZONE=@OLD_TIME_ZONE;
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT;
SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS;
SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION;
SET SQL_NOTES=@OLD_SQL_NOTES;
