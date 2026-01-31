package com.eduverse.eduversebe.controller;

import com.eduverse.eduversebe.common.api.ApiPaths;
import com.eduverse.eduversebe.common.api.ApiResponse;
import com.eduverse.eduversebe.common.globalEnums.SuccessCodes;
import com.eduverse.eduversebe.model.User;
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

    @GetMapping(ApiPaths.Courses.ROOT + "/{courseId}/videos/{videoId}")
    public ResponseEntity<?> getVideoStreamUrlForCourse(@AuthenticationPrincipal User user,
                                                        @PathVariable String courseId,
                                                        @PathVariable String videoId) {
        String userId = user == null ? null : user.getId();

        if (!videoService.isVideoAccessible(userId, courseId, videoId))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_VIDEO_SUCCESS,
                videoService.getVideoStreamUrl(courseId, videoId)
        ));
    }
}
