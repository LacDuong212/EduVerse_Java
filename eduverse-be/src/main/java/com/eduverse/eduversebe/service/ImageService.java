package com.eduverse.eduversebe.service;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final Cloudinary cloudinary;

    public Map<String, Object> getAvatarUploadParams(String userId) {
        return createUploadParams(
                "u_" + userId + "_avatar",
                "avatars",
                "w_500,h_500,c_fill,g_auto,f_auto,q_auto"
        );
    }

    public Map<String, Object> getCourseImageUploadParams(String courseId) {
        String publicId = "c_" + courseId + "_img-" + (System.currentTimeMillis() / 1000L);
        return createUploadParams(
                publicId,
                "courses",
                "w_1280,h_720,c_fill,g_auto,f_auto,q_auto,d_course_default_image"
        );
    }

    private Map<String, Object> createUploadParams(String publicId, String folder, String transformation) {
        Map<String, Object> params = new HashMap<>();
        params.put("timestamp", System.currentTimeMillis() / 1000L);
        params.put("public_id", publicId);
        params.put("folder", folder);
        params.put("transformation", transformation);

        params.put("overwrite", true);
        params.put("invalidate", true);
        params.put("allowed_formats", "jpg,jpeg,png,webp");

        String signature = cloudinary.apiSignRequest(
                params,
                cloudinary.config.apiSecret,
                1   // standard signature
        );

        Map<String, Object> response = new HashMap<>(params);
        response.put("signature", signature);
        response.put("api_key", cloudinary.config.apiKey);
        response.put("cloud_name", cloudinary.config.cloudName);

        return response;
    }
}
