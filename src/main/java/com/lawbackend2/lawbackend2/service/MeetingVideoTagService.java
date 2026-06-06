package com.lawbackend2.lawbackend2.service;

import com.lawbackend2.lawbackend2.entity.MeetingVideoTag;
import com.lawbackend2.lawbackend2.repository.MeetingVideoTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MeetingVideoTagService {

    @Autowired
    private MeetingVideoTagRepository meetingVideoTagRepository;

    // 创建视频标签
    public MeetingVideoTag createVideoTag(MeetingVideoTag videoTag) {
        videoTag.setIsDeleted(false);
        if (videoTag.getStatus() == null) {
            videoTag.setStatus("pending");
        }
        return meetingVideoTagRepository.save(videoTag);
    }

    // 根据ID获取视频标签
    public Optional<MeetingVideoTag> getVideoTagById(Long id) {
        return meetingVideoTagRepository.findById(id);
    }

    // 根据会议ID获取视频标签列表
    public List<MeetingVideoTag> getVideoTagsByMeetingId(Long meetingId) {
        return meetingVideoTagRepository.findByMeetingIdAndIsDeletedFalse(meetingId);
    }

    // 根据状态获取视频标签列表
    public List<MeetingVideoTag> getVideoTagsByStatus(String status) {
        return meetingVideoTagRepository.findByStatusAndIsDeletedFalse(status);
    }

    // 根据会议ID和状态获取视频标签列表
    public List<MeetingVideoTag> getVideoTagsByMeetingIdAndStatus(Long meetingId, String status) {
        return meetingVideoTagRepository.findByMeetingIdAndStatusAndIsDeletedFalse(meetingId, status);
    }

    // 根据文件ID获取视频标签
    public List<MeetingVideoTag> getVideoTagsByFileId(Long fileId) {
        return meetingVideoTagRepository.findByFileIdAndIsDeletedFalse(fileId);
    }

    // 更新视频标签
    public MeetingVideoTag updateVideoTag(Long id, MeetingVideoTag videoTagDetails) {
        Optional<MeetingVideoTag> optionalVideoTag = meetingVideoTagRepository.findById(id);
        if (optionalVideoTag.isPresent()) {
            MeetingVideoTag videoTag = optionalVideoTag.get();
            videoTag.setVideoTitle(videoTagDetails.getVideoTitle());
            videoTag.setStatus(videoTagDetails.getStatus());
            videoTag.setFileId(videoTagDetails.getFileId());
            videoTag.setUpdateUserId(videoTagDetails.getUpdateUserId());
            return meetingVideoTagRepository.save(videoTag);
        }
        return null;
    }

    // 绑定文件到视频标签
    public MeetingVideoTag bindFileToTag(Long tagId, Long fileId) {
        Optional<MeetingVideoTag> optionalVideoTag = meetingVideoTagRepository.findById(tagId);
        if (optionalVideoTag.isPresent()) {
            MeetingVideoTag videoTag = optionalVideoTag.get();
            videoTag.setFileId(fileId);
            videoTag.setStatus("generated");
            return meetingVideoTagRepository.save(videoTag);
        }
        return null;
    }

    // 软删除视频标签
    public boolean deleteVideoTag(Long id) {
        Optional<MeetingVideoTag> optionalVideoTag = meetingVideoTagRepository.findById(id);
        if (optionalVideoTag.isPresent()) {
            MeetingVideoTag videoTag = optionalVideoTag.get();
            videoTag.setIsDeleted(true);
            meetingVideoTagRepository.save(videoTag);
            return true;
        }
        return false;
    }

    // 更新视频标签状态
    public MeetingVideoTag updateVideoTagStatus(Long id, String status, Long userId) {
        Optional<MeetingVideoTag> optionalVideoTag = meetingVideoTagRepository.findById(id);
        if (optionalVideoTag.isPresent()) {
            MeetingVideoTag videoTag = optionalVideoTag.get();
            videoTag.setStatus(status);
            videoTag.setUpdateUserId(userId);
            return meetingVideoTagRepository.save(videoTag);
        }
        return null;
    }
}
