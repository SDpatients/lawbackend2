ALTER TABLE `tb_meeting_video_tag`
    ADD COLUMN `file_id` BIGINT NULL COMMENT '关联的文件记录ID，对应tb_file_record表主键' AFTER `status`;

CREATE INDEX `idx_file_id` ON `tb_meeting_video_tag` (`file_id`);