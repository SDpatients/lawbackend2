package com.lawbackend2.lawbackend2.repository;

import com.lawbackend2.lawbackend2.entity.MeetingVideoTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingVideoTagRepository extends JpaRepository<MeetingVideoTag, Long> {

    List<MeetingVideoTag> findByMeetingIdAndIsDeletedFalse(Long meetingId);

    List<MeetingVideoTag> findByStatusAndIsDeletedFalse(String status);

    List<MeetingVideoTag> findByMeetingIdAndStatusAndIsDeletedFalse(Long meetingId, String status);
}
