import axios from "axios";
import { useState, useEffect } from "react";

export const useVideoStream = (iocId, videoId) => {
  const backendUrl = import.meta.env.VITE_BACKEND_URL;
  const [streamUrl, setStreamUrl] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    setStreamUrl(null);
    setError(null);

    if (!iocId || !videoId) {
      setIsLoading(false);
      return;
    }

    const fetchStreamUrl = async () => {
      setIsLoading(true);
      try {
        const { data: response } = await axios.get(
          `${backendUrl}/api/courses/${iocId}/videos/${videoId}`,
          { withCredentials: true }
        );

        if (response && response.success) {
          setStreamUrl(response.result);
        } else {
          setError(response?.message || "Failed to retrieve stream URL");
        }
      } catch (err) {
        console.error("Video stream fetch error:", err);
        // Catch 403 Forbidden or other backend errors
        setError(err.response?.data?.message || "You do not have permission to view this video.");
      } finally {
        setIsLoading(false);
      }
    };

    fetchStreamUrl();
  }, [iocId, videoId, backendUrl]);

  return { streamUrl, isLoading, error };
};