package com.lawbackend2.lawbackend2.constant;

import java.util.Set;

public class VideoConstants {

    public static final String BIZ_TYPE_VIDEO = "VIDEO";
    public static final String BIZ_TYPE_VIDEO_EVIDENCE = "VIDEO_EVIDENCE";
    public static final String BIZ_TYPE_VIDEO_COURSE = "VIDEO_COURSE";
    public static final String BIZ_TYPE_VIDEO_MEETING = "VIDEO_MEETING";

    public static final String VIDEO_STATUS_PENDING = "PENDING";
    public static final String VIDEO_STATUS_PROCESSING = "PROCESSING";
    public static final String VIDEO_STATUS_COMPLETED = "COMPLETED";
    public static final String VIDEO_STATUS_FAILED = "FAILED";

    public static final Set<String> ALLOWED_VIDEO_TYPES = Set.of(
            "video/mp4",
            "video/avi",
            "video/x-msvideo",
            "video/quicktime",
            "video/x-ms-wmv",
            "video/x-flv",
            "video/x-matroska",
            "video/webm",
            "video/mpeg",
            "video/3gpp"
    );

    public static final Set<String> ALLOWED_VIDEO_EXTENSIONS = Set.of(
            "mp4", "avi", "mov", "wmv", "flv", "mkv", "webm", "mpeg", "mpg", "3gp"
    );

    public static final long MAX_VIDEO_SIZE = 500 * 1024 * 1024;

    public static boolean isVideoFile(String contentType, String fileExtension) {
        if (contentType != null && ALLOWED_VIDEO_TYPES.contains(contentType.toLowerCase())) {
            return true;
        }
        if (fileExtension != null) {
            return ALLOWED_VIDEO_EXTENSIONS.contains(fileExtension.toLowerCase());
        }
        return false;
    }
}
