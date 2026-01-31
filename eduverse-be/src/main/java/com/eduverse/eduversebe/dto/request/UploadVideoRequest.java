package com.eduverse.eduversebe.dto.request;

import lombok.Data;
import lombok.NonNull;

@Data
public class UploadVideoRequest {
    private String extension = "mp4";
    private String contentType = "video/mp4";
}
