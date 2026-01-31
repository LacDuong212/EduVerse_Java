package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.dto.response.VideoUploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3VideoService {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${video.upload-duration}")
    private Duration uploadDuration;

    @Value("${video.view-duration}")
    private Duration viewDuration;

    // allowed video types
    private static final Map<String, String> SUPPORTED_VIDEO_FORMATS = Map.of(
            "mp4", "video/mp4",
            "mov", "video/quicktime",
            "avi", "video/x-msvideo",
            "mkv", "video/x-matroska"
    );

    public String getKey(@DefaultValue("edv") String folderName, String videoId) {
        return "videos/" + folderName + "/" + videoId;
    }

    // UPLOAD: Generates a pre-signed URL for video upload only
    public VideoUploadResponse createVideoUploadUrl(@DefaultValue("edv") String folderName, String extension, String contentType) {

        String expectedMimeType = SUPPORTED_VIDEO_FORMATS.get(extension.toLowerCase());

        if (expectedMimeType == null) {
            throw new IllegalArgumentException("Extension '." + extension + "' is not supported. Use: mp4, mov, avi, mkv.");
        }

        if (!expectedMimeType.equals(contentType)) {
            throw new IllegalArgumentException("Content-Type mismatch. For ." + extension + " headers must be " + expectedMimeType);
        }

        String videoId = "LEC" + UUID.randomUUID() + "." + extension;
        String key = "videos/" + folderName + "/" + videoId;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(uploadDuration.getSeconds()))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        return VideoUploadResponse.builder()
                .url(presignedRequest.url().toString())
                .videoId(videoId)
                .build();
    }

    // DOWNLOAD/STREAM: Generates a URL to view the video
    public String createVideoViewUrl(String key) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(viewDuration.getSeconds()))
                .getObjectRequest(objectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }

    // DELETE: Permanently removes the video from S3
    public boolean deleteVideoByKey(String key) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);
            return true;

        } catch (S3Exception e) {
            log.error("S3 Delete Error: {}", e.awsErrorDetails().errorMessage());
            return false;
        }
    }
}
