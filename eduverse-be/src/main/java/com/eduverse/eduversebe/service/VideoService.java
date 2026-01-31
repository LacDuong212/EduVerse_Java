package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.common.exception.AppException;
import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import com.eduverse.eduversebe.dto.response.VideoUploadResponse;
import com.eduverse.eduversebe.model.Course;
import com.eduverse.eduversebe.model.DraftVideo;
import com.eduverse.eduversebe.repository.CourseRepository;
import com.eduverse.eduversebe.repository.DraftVideoRepository;
import com.eduverse.eduversebe.repository.InstructorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final CourseRepository courseRepository;
    private final DraftVideoRepository draftVideoRepository;
    private final InstructorRepository instructorRepository;
    private final S3VideoService s3VideoService;

    @Value("${video.draft-duration:24h}")
    private Duration videoDraftDuration;

    public boolean isVideoAccessible(String userId, String courseId, String videoId) {
        // #TODO: check video accessibility logic
        return true;
    }

    public VideoUploadResponse getVideoUploadUrl(String userId, String extension, String contentType) {
        VideoUploadResponse res = s3VideoService.createVideoUploadUrl(userId, extension, contentType);

        draftVideoRepository.save(
                DraftVideo.builder()
                        .key(s3VideoService.getKey(userId, res.getVideoId()))
                        .contentType(contentType)
                        .createdAt(Instant.now())
                        .expireAt(Instant.now().plus(videoDraftDuration))
                        .build()
        );

        return res;
    }

    public String getVideoStreamUrl(String instructorOrCourseId, String videoId) {
        String instructorId;
        if (instructorRepository.existsByUserId(instructorOrCourseId)) {
            instructorId = instructorOrCourseId;
        } else {
            Course course = courseRepository.findById(instructorOrCourseId)
                    .orElseThrow(() -> new AppException(ErrorCodes.COURSE_NOT_FOUND));
            instructorId = course.getInstructor().getRef();
        }

        return s3VideoService.createVideoViewUrl(
                s3VideoService.getKey(instructorId, videoId)
        );
    }
}
