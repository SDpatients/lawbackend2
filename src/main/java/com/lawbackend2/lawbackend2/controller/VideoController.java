package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.common.Result;
import com.lawbackend2.lawbackend2.constant.VideoConstants;
import com.lawbackend2.lawbackend2.entity.FileRecord;
import com.lawbackend2.lawbackend2.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Tag(name = "视频管理")
@RestController
@RequestMapping("/video")
@Validated
public class VideoController {

    private final FileService fileService;

    @Value("${file.upload.path:D:\\law-upload}")
    private String uploadPath;

    @Autowired
    public VideoController(FileService fileService) {
        this.fileService = fileService;
    }

    @Operation(summary = "视频上传")
    @PostMapping("/upload")
    public Result<FileRecord> uploadVideo(
            @Parameter(description = "视频文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "业务类型") @RequestParam("bizType") String bizType,
            @Parameter(description = "业务ID") @RequestParam("bizId") String bizId) {

        FileRecord fileRecord = fileService.uploadVideo(file, bizType, bizId);
        return Result.success(fileRecord);
    }

    @Operation(summary = "视频流式播放（支持断点续传）")
    @GetMapping("/stream/{fileId}")
    public ResponseEntity<Resource> streamVideo(
            @Parameter(description = "视频文件ID") @PathVariable Long fileId,
            HttpServletRequest request) throws IOException {

        FileRecord fileRecord = fileService.getFileInfo(fileId);

        if (!fileService.isVideoFile(fileId)) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }

        File videoFile = new File(fileRecord.getFilePath());
        if (!videoFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        long fileLength = videoFile.length();
        String rangeHeader = request.getHeader(HttpHeaders.RANGE);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fileRecord.getMimeType()));
        headers.set("Accept-Ranges", "bytes");

        if (rangeHeader == null) {
            headers.setContentLength(fileLength);
            headers.setContentDispositionFormData("inline", fileRecord.getOriginalFileName());
            Resource resource = new FileSystemResource(videoFile);
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        }

        long start = 0;
        long end = fileLength - 1;

        if (rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.substring(6).split("-");
            try {
                start = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    end = Long.parseLong(ranges[1]);
                }
            } catch (NumberFormatException e) {
                log.error("解析Range头失败: {}", rangeHeader);
            }
        }

        if (end >= fileLength) {
            end = fileLength - 1;
        }

        long contentLength = end - start + 1;

        headers.setContentLength(contentLength);
        headers.set(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileLength);
        headers.setContentDispositionFormData("inline", fileRecord.getOriginalFileName());

        Resource resource = new PartialFileResource(videoFile, start, contentLength);

        return new ResponseEntity<>(resource, headers, HttpStatus.PARTIAL_CONTENT);
    }

    @Operation(summary = "视频下载")
    @GetMapping("/download/{fileId}")
    public void downloadVideo(
            @Parameter(description = "视频文件ID") @PathVariable Long fileId,
            HttpServletResponse response) throws IOException {

        FileRecord fileRecord = fileService.getFileInfo(fileId);

        if (!fileService.isVideoFile(fileId)) {
            response.setStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
            return;
        }

        File videoFile = new File(fileRecord.getFilePath());
        if (!videoFile.exists()) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        String contentType = fileRecord.getMimeType();
        if (contentType == null) {
            contentType = "video/mp4";
        }

        response.setContentType(contentType);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileRecord.getOriginalFileName() + "\"");
        response.setContentLengthLong(videoFile.length());

        Files.copy(videoFile.toPath(), response.getOutputStream());
    }

    @Operation(summary = "获取视频信息")
    @GetMapping("/info/{fileId}")
    public Result<FileRecord> getVideoInfo(
            @Parameter(description = "视频文件ID") @PathVariable Long fileId) {

        FileRecord fileRecord = fileService.getFileInfo(fileId);

        if (!fileService.isVideoFile(fileId)) {
            throw new RuntimeException("该文件不是视频文件");
        }

        return Result.success(fileRecord);
    }

    @Operation(summary = "获取支持的视频格式列表")
    @GetMapping("/formats")
    public Result<List<String>> getSupportedFormats() {
        return Result.success(Arrays.asList(
                "MP4", "AVI", "MOV", "WMV", "FLV", "MKV", "WEBM", "MPEG", "3GP"
        ));
    }

    private static class PartialFileResource extends FileSystemResource {
        private final long start;
        private final long length;

        public PartialFileResource(File file, long start, long length) {
            super(file);
            this.start = start;
            this.length = length;
        }

        @Override
        public long contentLength() throws IOException {
            return length;
        }

        @Override
        public java.io.InputStream getInputStream() throws IOException {
            RandomAccessFile randomAccessFile = new RandomAccessFile(getFile(), "r");
            randomAccessFile.seek(start);
            return new PartialInputStream(randomAccessFile, length);
        }
    }

    private static class PartialInputStream extends java.io.InputStream {
        private final RandomAccessFile randomAccessFile;
        private long remaining;

        public PartialInputStream(RandomAccessFile randomAccessFile, long length) {
            this.randomAccessFile = randomAccessFile;
            this.remaining = length;
        }

        @Override
        public int read() throws IOException {
            if (remaining <= 0) {
                return -1;
            }
            remaining--;
            return randomAccessFile.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (remaining <= 0) {
                return -1;
            }
            int toRead = (int) Math.min(len, remaining);
            int read = randomAccessFile.read(b, off, toRead);
            if (read > 0) {
                remaining -= read;
            }
            return read;
        }

        @Override
        public void close() throws IOException {
            randomAccessFile.close();
        }
    }
}
