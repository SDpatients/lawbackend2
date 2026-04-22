-- 案件任务提交记录表
-- 用于记录每个任务的多次提交，每次提交包含标题、内容和文件

CREATE TABLE IF NOT EXISTS `tb_case_task_submission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `case_task_id` BIGINT NOT NULL COMMENT '任务ID，关联tb_case_task表',
  `submission_title` VARCHAR(500) NOT NULL COMMENT '提交标题',
  `submission_content` TEXT COMMENT '提交内容',
  `submission_type` VARCHAR(50) DEFAULT 'NORMAL' COMMENT '提交类型：NORMAL-普通提交，REVISION-修订提交',
  `submission_number` INT DEFAULT 1 COMMENT '提交序号，同一个任务的第几次提交',
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '提交状态：PENDING-待审核，APPROVED-已通过，REJECTED-已驳回',
  `reviewer_id` BIGINT COMMENT '审核人ID',
  `review_opinion` TEXT COMMENT '审核意见',
  `review_time` DATETIME COMMENT '审核时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user_id` BIGINT COMMENT '创建人ID',
  `update_user_id` BIGINT COMMENT '更新人ID',
  `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '记录状态：ACTIVE-有效，DELETED-已删除',
  `is_deleted` BOOLEAN DEFAULT FALSE COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_case_task_id` (`case_task_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_status` (`status`),
  KEY `idx_create_user_id` (`create_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='案件任务提交记录表';

-- 添加外键约束
ALTER TABLE `tb_case_task_submission`
ADD CONSTRAINT `fk_case_task_submission_task`
FOREIGN KEY (`case_task_id`) REFERENCES `tb_case_task` (`id`) ON DELETE CASCADE;
