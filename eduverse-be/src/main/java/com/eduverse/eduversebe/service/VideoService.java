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
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final CourseRepository courseRepository;
    private final DraftVideoRepository draftVideoRepository;
    private final InstructorRepository instructorRepository;
    private final S3VideoService s3VideoService;

    public boolean isVideoAccessible(String userId, String courseId, String videoId) {
        // #TODO: check video accessibility logic, check if user.myCourses have courseId and if the lecture is paid
        return true;
    }

    public VideoUploadResponse getVideoUploadUrl(String userId, String extension, String contentType) {
        VideoUploadResponse res = s3VideoService.createVideoUploadUrl(userId, extension, contentType);

        draftVideoRepository.save(
                DraftVideo.builder()
                        .key(s3VideoService.getKey(userId, res.getVideoId()))
                        .contentType(contentType)
                        .createdAt(Instant.now())
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
