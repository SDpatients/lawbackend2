package com.lawbackend2.lawbackend2.dto;

import lombok.Data;
import java.util.List;

/**
 * OnlyOffice 回调请求 DTO
 */
@Data
public class OnlyOfficeCallbackRequest {

    /**
     * 操作类型：0-无操作，1-下载，2-编辑，3-保存
     */
    private Integer action;

    /**
     * 回调数据
     */
    private CallbackData data;

    /**
     * 变更历史
     */
    private List<ChangeHistory> changes;

    /**
     * JWT 令牌
     */
    private String token;

    @Data
    public static class CallbackData {
        /**
         * 文件标题
         */
        private String title;

        /**
         * 文件下载地址
         */
        private String url;

        /**
         * 文件版本
         */
        private Integer version;

        /**
         * 修改用户列表
         */
        private List<String> users;

        /**
         * 键值
         */
        private String key;
    }

    @Data
    public static class ChangeHistory {
        /**
         * 变更时间
         */
        private String created;

        /**
         * 变更用户
         */
        private String user;

        /**
         * 变更描述
         */
        private String changes;
    }
}
