package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.entity.MeetingVoteItem;
import com.lawbackend2.lawbackend2.repository.MeetingVoteItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MeetingVoteItemService {

    @Autowired
    private MeetingVoteItemRepository meetingVoteItemRepository;

    // 创建投票项
    public MeetingVoteItem createVoteItem(MeetingVoteItem voteItem) {
        voteItem.setIsDeleted(false);
        voteItem.setStatus("ACTIVE");
        return meetingVoteItemRepository.save(voteItem);
    }

    // 根据ID获取投票项
    public Optional<MeetingVoteItem> getVoteItemById(Long id) {
        return meetingVoteItemRepository.findById(id);
    }

    // 根据会议ID获取投票项列表
    public List<MeetingVoteItem> getVoteItemsByMeetingId(Long meetingId) {
        return meetingVoteItemRepository.findByMeetingIdAndIsDeletedFalse(meetingId);
    }

    // 根据状态获取投票项列表
    public List<MeetingVoteItem> getVoteItemsByStatus(String status) {
        return meetingVoteItemRepository.findByStatusAndIsDeletedFalse(status);
    }

    // 根据会议ID和状态获取投票项列表
    public List<MeetingVoteItem> getVoteItemsByMeetingIdAndStatus(Long meetingId, String status) {
        return meetingVoteItemRepository.findByMeetingIdAndStatusAndIsDeletedFalse(meetingId, status);
    }

    // 更新投票项
    public MeetingVoteItem updateVoteItem(Long id, MeetingVoteItem voteItemDetails) {
        Optional<MeetingVoteItem> optionalVoteItem = meetingVoteItemRepository.findById(id);
        if (optionalVoteItem.isPresent()) {
            MeetingVoteItem voteItem = optionalVoteItem.get();
            voteItem.setItemName(voteItemDetails.getItemName());
            voteItem.setAgreeCount(voteItemDetails.getAgreeCount());
            voteItem.setOpposeCount(voteItemDetails.getOpposeCount());
            voteItem.setAbstainCount(voteItemDetails.getAbstainCount());
            voteItem.setRemark(voteItemDetails.getRemark());
            voteItem.setStatus(voteItemDetails.getStatus());
            voteItem.setUpdateUserId(voteItemDetails.getUpdateUserId());
            return meetingVoteItemRepository.save(voteItem);
        }
        return null;
    }

    // 软删除投票项
    public boolean deleteVoteItem(Long id) {
        Optional<MeetingVoteItem> optionalVoteItem = meetingVoteItemRepository.findById(id);
        if (optionalVoteItem.isPresent()) {
            MeetingVoteItem voteItem = optionalVoteItem.get();
            voteItem.setIsDeleted(true);
            meetingVoteItemRepository.save(voteItem);
            return true;
        }
        return false;
    }

    // 处理投票
    public MeetingVoteItem castVote(Long id, String voteType, Long userId) {
        Optional<MeetingVoteItem> optionalVoteItem = meetingVoteItemRepository.findById(id);
        if (optionalVoteItem.isPresent()) {
            MeetingVoteItem voteItem = optionalVoteItem.get();
            switch (voteType) {
                case "agree":
                    voteItem.setAgreeCount(voteItem.getAgreeCount() + 1);
                    break;
                case "oppose":
                    voteItem.setOpposeCount(voteItem.getOpposeCount() + 1);
                    break;
                case "abstain":
                    voteItem.setAbstainCount(voteItem.getAbstainCount() + 1);
                    break;
            }
            voteItem.setUpdateUserId(userId);
            return meetingVoteItemRepository.save(voteItem);
        }
        return null;
    }
}
