import { useState } from "react";
import axios from "axios"; // Assuming axios, but fetch works too

// CONSTANTS
const backendUrl = import.meta.env.VITE_BACKEND_URL;
const INSTRUCTOR_UPLOAD_ENDPOINT = `${backendUrl}/api/instructor/videos`;
const VIEW_ENDPOINT = (courseId, videoId) => `${backendUrl}/api/courses/${courseId}/videos/${videoId}`;

export const useVideoTest = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [uploadProgress, setUploadProgress] = useState(0);

  const uploadVideo = async (file) => {
    setLoading(true);
    setError(null);
    setUploadProgress(0);

    try {
      const extension = file.name.split(".").pop();
      const contentType = file.type;

      const response = await axios.post(
        INSTRUCTOR_UPLOAD_ENDPOINT,
        { extension: extension, contentType: contentType },
        { withCredentials: true }
      );

      const { url, videoId } = response.data.result;

      console.log("Got Presigned URL:", url);

      // upload to S3
      await axios.put(url, file, {
        headers: {
          "Content-Type": contentType
        },
        onUploadProgress: (progressEvent) => {
          const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          setUploadProgress(percentCompleted);
        }
      });

      return videoId;

    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || err.message);
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const getVideoUrl = async (courseId, videoId) => {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get(
        VIEW_ENDPOINT(courseId, videoId),
        { withCredentials: true }
      );

      return response.data.result;

    } catch (err) {
      setError(err.response?.data?.message || err.message);
      return null;
    } finally {
      setLoading(false);
    }
  };

  return {
    uploadVideo,
    getVideoUrl,
    loading,
    error,
    uploadProgress
  };
};