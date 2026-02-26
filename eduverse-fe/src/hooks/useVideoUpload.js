import axios from "axios";
import { useState, useCallback } from "react";
import { toast } from "react-toastify";

export const useVideoUpload = () => {
  const backendUrl = import.meta.env.VITE_BACKEND_URL;
  const [progress, setProgress] = useState(0);
  const [isUploading, setIsUploading] = useState(false);
  const [error, setError] = useState(null);

  const uploadVideo = useCallback(async (file, onSuccess) => {
    if (!file) {
      toast.error("No video file selected");
      return;
    }

    setIsUploading(true);
    setProgress(0);
    setError(null);

    const toastId = toast.loading("Preparing upload...");

    try {
      const allowedExts = ['mp4', 'mov', 'avi', 'mkv'];
      const fileExt = file.name.split('.').pop().toLowerCase() || "mp4";

      if (!allowedExts.includes(fileExt)) {
        toast.error("Invalid file format. Please upload MP4, MOV, AVI, or MKV.");
        e.target.value = "";
        return;
      }

      const { data: response } = await axios.post(
        `${backendUrl}/api/instructor/videos`,
        {
          extension: fileExt,
          contentType: file.type || "video/mp4"
        },
        { withCredentials: true }
      );

      if (!response.success) {
        throw new Error(response.message || "Failed to get upload permission");
      }

      const { url: uploadUrl, videoId } = response.result;

      toast.update(toastId, { render: "Uploading to storage...", type: "info", isLoading: true });

      const s3Axios = axios.create();
      await s3Axios.put(uploadUrl, file, {
        headers: { 'Content-Type': file.type },
        onUploadProgress: (progressEvent) => {
          const percentCompleted = Math.round(
            (progressEvent.loaded * 100) / progressEvent.total
          );
          setProgress(percentCompleted);
        },
      });

      toast.update(toastId, {
        render: "Video uploaded successfully!",
        type: "success",
        isLoading: false,
        autoClose: 2000
      });

      if (onSuccess) onSuccess(videoId);
      return videoId;

    } catch (err) {
      console.error("Upload failed:", err);
      const errMsg = err.response?.data?.message || err.message || "Upload failed";
      setError(errMsg);

      toast.update(toastId, {
        render: errMsg,
        type: "error",
        isLoading: false,
        autoClose: 3000
      });
    } finally {
      setIsUploading(false);
    }
  }, [backendUrl]);

  return { uploadVideo, progress, isUploading, error };
};