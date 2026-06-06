package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.entity.MeetingVideoTag;
import com.lawbackend2.lawbackend2.service.MeetingVideoTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/video-tags")
public class MeetingVideoTagController {

    @Autowired
    private MeetingVideoTagService meetingVideoTagService;

    @PostMapping
    public ResponseEntity<MeetingVideoTag> createVideoTag(@RequestBody MeetingVideoTag videoTag) {
        MeetingVideoTag createdVideoTag = meetingVideoTagService.createVideoTag(videoTag);
        return new ResponseEntity<>(createdVideoTag, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingVideoTag> getVideoTagById(@PathVariable Long id) {
        Optional<MeetingVideoTag> videoTag = meetingVideoTagService.getVideoTagById(id);
        return videoTag.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/meeting/{meetingId}")
    public ResponseEntity<List<MeetingVideoTag>> getVideoTagsByMeetingId(@PathVariable Long meetingId) {
        List<MeetingVideoTag> videoTags = meetingVideoTagService.getVideoTagsByMeetingId(meetingId);
        return ResponseEntity.ok(videoTags);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<MeetingVideoTag>> getVideoTagsByStatus(@PathVariable String status) {
        List<MeetingVideoTag> videoTags = meetingVideoTagService.getVideoTagsByStatus(status);
        return ResponseEntity.ok(videoTags);
    }

    @GetMapping("/meeting/{meetingId}/status/{status}")
    public ResponseEntity<List<MeetingVideoTag>> getVideoTagsByMeetingIdAndStatus(
            @PathVariable Long meetingId, @PathVariable String status) {
        List<MeetingVideoTag> videoTags = meetingVideoTagService.getVideoTagsByMeetingIdAndStatus(meetingId, status);
        return ResponseEntity.ok(videoTags);
    }

    @GetMapping("/file/{fileId}")
    public ResponseEntity<List<MeetingVideoTag>> getVideoTagsByFileId(@PathVariable Long fileId) {
        List<MeetingVideoTag> videoTags = meetingVideoTagService.getVideoTagsByFileId(fileId);
        return ResponseEntity.ok(videoTags);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MeetingVideoTag> updateVideoTag(@PathVariable Long id, @RequestBody MeetingVideoTag videoTagDetails) {
        MeetingVideoTag updatedVideoTag = meetingVideoTagService.updateVideoTag(id, videoTagDetails);
        return updatedVideoTag != null ? ResponseEntity.ok(updatedVideoTag) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/bind-file")
    public ResponseEntity<MeetingVideoTag> bindFileToTag(
            @PathVariable Long id, @RequestParam Long fileId) {
        MeetingVideoTag updatedVideoTag = meetingVideoTagService.bindFileToTag(id, fileId);
        return updatedVideoTag != null ? ResponseEntity.ok(updatedVideoTag) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideoTag(@PathVariable Long id) {
        boolean deleted = meetingVideoTagService.deleteVideoTag(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<MeetingVideoTag> updateVideoTagStatus(
            @PathVariable Long id, @RequestParam String status, @RequestParam Long userId) {
        MeetingVideoTag updatedVideoTag = meetingVideoTagService.updateVideoTagStatus(id, status, userId);
        return updatedVideoTag != null ? ResponseEntity.ok(updatedVideoTag) : ResponseEntity.notFound().build();
    }
}