-- 为文件记录表添加视频相关字段
-- 用于支持视频上传和管理功能

ALTER TABLE `tb_file_record`
    ADD COLUMN `video_duration` BIGINT NULL COMMENT '视频时长（秒）',
    ADD COLUMN `video_width` INT NULL COMMENT '视频宽度（像素）',
    ADD COLUMN `video_height` INT NULL COMMENT '视频高度（像素）',
    ADD COLUMN `thumbnail_path` VARCHAR(500) NULL COMMENT '视频缩略图路径',
    ADD COLUMN `video_status` VARCHAR(20) NULL COMMENT '视频处理状态: PENDING-待处理, PROCESSING-处理中, COMPLETED-已完成, FAILED-失败';

-- 添加索引优化视频查询
CREATE INDEX `idx_video_status` ON `tb_file_record` (`video_status`);
