package com.lawbackend2.lawbackend2.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "backup")
public class BackupProperties {

    private boolean enabled = true;

    private String cron = "0 0 2 * * ?";

    private String path = "D:\\law-backup";

    private int retentionDays = 30;

    private String mysqldumpPath = "mysqldump";

    private String host = "localhost";

    private String port = "3306";

    private String username = "root";

    private String password = "";

    private String database = "law";

    private boolean compress = true;
}
