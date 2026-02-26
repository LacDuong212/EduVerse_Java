import axios from "axios";
import { useState, useCallback } from "react";

export const useImageUpload = () => {
  const backendUrl = import.meta.env.VITE_BACKEND_URL;
  const [imageUrl, setImageUrl] = useState(null);
  const [isUploading, setIsUploading] = useState(false);
  const [error, setError] = useState(null);

  const uploadImage = useCallback(async (file, courseId) => {
    if (!file) throw new Error("No image to upload");

    setIsUploading(true);
    setError(null);
    setImageUrl(null);

    try {
      const { data: response } = await axios.get(
        `${backendUrl}/api/courses/${courseId}/images`,
        { withCredentials: true }
      );

      if (!response.success) {
        throw new Error(response.message || "Failed to get upload permission");
      }

      const { cloud_name, api_key, ...signatureParams } = response.result;

      const formData = new FormData();
      formData.append("file", file);
      formData.append("api_key", api_key);

      Object.keys(signatureParams).forEach((key) => {
        formData.append(key, signatureParams[key]);
      });

      const cloudinaryRes = await axios.post(
        `https://api.cloudinary.com/v1_1/${cloud_name}/image/upload`,
        formData
      );

      const secureUrl = cloudinaryRes.data.secure_url;
      setImageUrl(secureUrl);

      return secureUrl;

    } catch (err) {
      console.error("Upload error:", err);
      const errMsg = err.response?.data?.message || err.message || "Upload image failed";
      setError(errMsg);
      throw new Error(errMsg);
    } finally {
      setIsUploading(false);
    }
  }, [backendUrl]);

  return { uploadImage, isUploading, error, imageUrl };
};