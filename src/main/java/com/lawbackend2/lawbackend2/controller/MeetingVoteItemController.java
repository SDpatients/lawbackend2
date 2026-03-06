package com.lawbackend2.lawbackend2.controller;

import com.lawbackend2.lawbackend2.entity.MeetingVoteItem;
import com.lawbackend2.lawbackend2.service.MeetingVoteItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vote-items")
public class MeetingVoteItemController {

    @Autowired
    private MeetingVoteItemService meetingVoteItemService;

    // 创建投票项
    @PostMapping
    public ResponseEntity<MeetingVoteItem> createVoteItem(@RequestBody MeetingVoteItem voteItem) {
        MeetingVoteItem createdVoteItem = meetingVoteItemService.createVoteItem(voteItem);
        return new ResponseEntity<>(createdVoteItem, HttpStatus.CREATED);
    }

    // 根据ID获取投票项
    @GetMapping("/{id}")
    public ResponseEntity<MeetingVoteItem> getVoteItemById(@PathVariable Long id) {
        Optional<MeetingVoteItem> voteItem = meetingVoteItemService.getVoteItemById(id);
        return voteItem.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 根据会议ID获取投票项列表
    @GetMapping("/meeting/{meetingId}")
    public ResponseEntity<List<MeetingVoteItem>> getVoteItemsByMeetingId(@PathVariable Long meetingId) {
        List<MeetingVoteItem> voteItems = meetingVoteItemService.getVoteItemsByMeetingId(meetingId);
        return ResponseEntity.ok(voteItems);
    }

    // 根据状态获取投票项列表
    @GetMapping("/status/{status}")
    public ResponseEntity<List<MeetingVoteItem>> getVoteItemsByStatus(@PathVariable String status) {
        List<MeetingVoteItem> voteItems = meetingVoteItemService.getVoteItemsByStatus(status);
        return ResponseEntity.ok(voteItems);
    }

    // 根据会议ID和状态获取投票项列表
    @GetMapping("/meeting/{meetingId}/status/{status}")
    public ResponseEntity<List<MeetingVoteItem>> getVoteItemsByMeetingIdAndStatus(
            @PathVariable Long meetingId, @PathVariable String status) {
        List<MeetingVoteItem> voteItems = meetingVoteItemService.getVoteItemsByMeetingIdAndStatus(meetingId, status);
        return ResponseEntity.ok(voteItems);
    }

    // 更新投票项
    @PutMapping("/{id}")
    public ResponseEntity<MeetingVoteItem> updateVoteItem(@PathVariable Long id, @RequestBody MeetingVoteItem voteItemDetails) {
        MeetingVoteItem updatedVoteItem = meetingVoteItemService.updateVoteItem(id, voteItemDetails);
        return updatedVoteItem != null ? ResponseEntity.ok(updatedVoteItem) : ResponseEntity.notFound().build();
    }

    // 删除投票项（软删除）
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoteItem(@PathVariable Long id) {
        boolean deleted = meetingVoteItemService.deleteVoteItem(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // 处理投票
    @PostMapping("/{id}/vote")
    public ResponseEntity<MeetingVoteItem> castVote(
            @PathVariable Long id, @RequestParam String voteType, @RequestParam Long userId) {
        MeetingVoteItem updatedVoteItem = meetingVoteItemService.castVote(id, voteType, userId);
        return updatedVoteItem != null ? ResponseEntity.ok(updatedVoteItem) : ResponseEntity.notFound().build();
    }
}
