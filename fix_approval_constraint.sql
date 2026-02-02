ALTER TABLE tb_approval DROP INDEX uk_case_type;

ALTER TABLE tb_approval ADD INDEX idx_case_type (case_id, approval_type);