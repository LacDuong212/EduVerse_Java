import { useEffect, useCallback, useMemo, useState, useRef } from "react";
import { useDropzone } from "react-dropzone";
import { useSelector } from "react-redux";
import { toast } from "react-toastify";
import { useImageUpload } from "@/hooks/useImageUpload";
import { useVideoStream } from "@/hooks/useVideoStream";
import { useVideoUpload } from "@/hooks/useVideoUpload";

export const useStep2 = (draftData, onSave, stepperInstance) => {
  const iocId = draftData.id || useSelector((state) => state.auth.userData?.id) || null;
  const imageInputRef = useRef(null);
  const videoInputRef = useRef(null);

  const { uploadImage, isUploading: isImgLoading } = useImageUpload();
  const { uploadVideo, progress: vidProgress, isUploading: isVidLoading } = useVideoUpload();

  // states
  const [imageState, setImageState] = useState({
    tab: draftData.image instanceof File ? "upload" : "url",
    url: typeof draftData.image === "string" ? draftData.image : '',
    file: draftData.image instanceof File ? draftData.image : null,
  });

  const [videoState, setVideoState] = useState({
    videoId: typeof draftData.previewVideo === "string" ? draftData.previewVideo : '',
    file: draftData.previewVideo instanceof File ? draftData.previewVideo : null,
  });

  const [error, setError] = useState('');
  const [previews, setPreviews] = useState({ image: null, video: null });

  useEffect(() => {
    const imgUrl = imageState.file ? URL.createObjectURL(imageState.file) : imageState.url;
    const vidUrl = videoState.file ? URL.createObjectURL(videoState.file) : null;

    setPreviews({ image: imgUrl, video: vidUrl });

    return () => {
      if (imageState.file) URL.revokeObjectURL(imgUrl);
      if (videoState.file) URL.revokeObjectURL(vidUrl);
    };
  }, [imageState.file, imageState.url, videoState.file]);

  const isBusy = isImgLoading || isVidLoading;

  const isS3Reference = useMemo(() => {
    if (!videoState.videoId || videoState.file) return false;
    const isNewFormat = videoState.videoId.startsWith("LEC");
    const isLegacyFormat = /^1766.*\.mp4$/.test(videoState.videoId);
    return isNewFormat || isLegacyFormat;
  }, [videoState.videoId, videoState.file]);

  const { streamUrl: s3StreamUrl, loading: streamLoading } = useVideoStream(
    iocId,
    isS3Reference ? videoState.videoId : null
  );

  // handlers
  const handleImageFileChange = useCallback((fileOrEvent) => {
    const file = fileOrEvent?.target?.files ? fileOrEvent.target.files[0] : fileOrEvent;
    if (!file) return;

    const maxSize = 5 * 1024 * 1024;
    if (file.size > maxSize) {
      toast.error("Image is too large. Max size is 5MB.");
      return;
    }

    setImageState(p => ({ ...p, file, url: '', tab: "upload" }));
    setError('');
  }, []);

  const handleRemoveImage = useCallback(() => {
    setImageState(p => ({ ...p, url: '', file: null }));
    if (imageInputRef.current) imageInputRef.current.value = '';
  }, []);

  const handleVideoFileChange = (e) => {
    const file = e.target.files && e.target.files[0];
    if (!file) return;

    const maxVidSize = 1024 * 1024 * 1024; 
    
    if (file.size > maxVidSize) {
      toast.error("Video is too large. Maximum size is 1GB.");
      e.target.value = "";
      return;
    }

    const allowedExts = ['mp4', 'mov', 'avi', 'mkv'];
    const fileExt = file.name.split('.').pop().toLowerCase();
  

    if (!allowedExts.includes(fileExt)) {
      toast.error("Invalid file format. Please upload MP4, MOV, AVI, or MKV.");
      e.target.value = "";
      return;
    }

    setVideoState({ file, videoId: '' });
    setError('');
  };

  const handleRemoveVideo = useCallback(() => {
    setVideoState({ file: null, videoId: '' });
    if (videoInputRef.current) videoInputRef.current.value = '';
  }, []);

  // dropzone setup (for image)
  const onDrop = useCallback((acceptedFiles, rejectedFiles) => {
    if (rejectedFiles.length > 0) {
      toast.error("Please upload a valid image file (JPG, PNG, WebP)");
      return;
    }
    if (acceptedFiles.length > 0) {
      handleImageFileChange(acceptedFiles[0]);
    }
  }, [handleImageFileChange]);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: { "image/*": [".jpeg", ".jpg", ".png", ".webp"] },
    multiple: false,
    disabled: isImgLoading
  });

  const handleSubmit = async (e) => {
    if (e) e.preventDefault();
    if (isBusy) return;
    setError('');

    const currentImage = imageState.tab === "url" ? imageState.url : imageState.file;
    if (!currentImage) {
      toast.error("Course image is required");
      return setError("Please provide a course image.");
    }

    try {
      let finalImg = imageState.url;

      if (imageState.tab === "upload" && imageState.file) {
        finalImg = await uploadImage(imageState.file, iocId);
      }

      const finalizeSave = (finalVidId) => {
        onSave({
          image: finalImg,
          previewVideo: finalVidId,
          thumbnail: finalImg,
        });
        toast.success("Step 2 saved!");
        stepperInstance?.next();
      };

      if (videoState.file) {
        await uploadVideo(videoState.file, finalizeSave);
      } else {
        finalizeSave(videoState.videoId);
      }
    } catch (err) {
      console.error("Upload error:", err);
      toast.error("Upload failed");
      setError("Failed to upload media");
    }
  };

  return {
    state: { 
      imageState, 
      videoState, 
      error, 
      isBusy, 
      isS3Reference, 
      vidProgress, 
      isImgLoading, 
      isVidLoading, 
      streamLoading 
    },
    previews: { 
      previewImage: previews.image, 
      videoObjectUrl: previews.video, 
      s3StreamUrl 
    },
    refs: { 
      imageInputRef, 
      videoInputRef 
    },
    dropzone: { 
      getRootProps, 
      getInputProps, 
      isDragActive 
    },
    methods: {
      setImageState,
      setVideoState,
      handleImageFileChange,
      handleVideoFileChange,
      handleRemoveVideo,
      handleRemoveImage,
      handleSubmit,
      setError
    }
  };
};