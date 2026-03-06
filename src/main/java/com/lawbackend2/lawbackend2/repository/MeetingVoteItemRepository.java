package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.MeetingVoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingVoteItemRepository extends JpaRepository<MeetingVoteItem, Long> {

    List<MeetingVoteItem> findByMeetingIdAndIsDeletedFalse(Long meetingId);

    List<MeetingVoteItem> findByStatusAndIsDeletedFalse(String status);

    List<MeetingVoteItem> findByMeetingIdAndStatusAndIsDeletedFalse(Long meetingId, String status);
}
