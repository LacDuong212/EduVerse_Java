package com.eduverse.eduversebe.controller;

import com.eduverse.eduversebe.common.api.ApiPaths;
import com.eduverse.eduversebe.common.api.ApiResponse;
import com.eduverse.eduversebe.common.globalEnums.SuccessCodes;
import com.eduverse.eduversebe.service.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    @GetMapping(ApiPaths.Courses.ROOT + "/{instructorOrCourseId}/videos/{videoId}")
    public ResponseEntity<?> getVideoStreamUrlForCourse(@AuthenticationPrincipal(expression = "id") String userId,
                                                        @PathVariable String instructorOrCourseId,
                                                        @PathVariable String videoId) {
        if (!videoService.isVideoAccessible(userId, instructorOrCourseId, videoId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_VIDEO_SUCCESS,
                videoService.getVideoStreamUrl(instructorOrCourseId, videoId)
        ));
    }
}
