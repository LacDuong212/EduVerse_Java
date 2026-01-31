import { useState } from "react";
import { useVideoTest } from "./useVideoTest";

const VideoTest = () => {
  const { uploadVideo, getVideoUrl, loading, error, uploadProgress } = useVideoTest();

  const [ownerId, setOwnerId] = useState("tmp");
  const [selectedFile, setSelectedFile] = useState(null);

  const [uploadedVideoId, setUploadedVideoId] = useState("");
  const [playbackUrl, setPlaybackUrl] = useState("");

  const handleFileChange = (e) => {
    setSelectedFile(e.target.files[0]);
  };

  const handleUpload = async () => {
    if (!selectedFile) return alert("Please select a file first");

    try {
      const videoId = await uploadVideo(selectedFile);
      setUploadedVideoId(videoId);
    } catch (e) {
      // error is handled in hook
    }
  };

  const handleView = async () => {
    if (!ownerId || !uploadedVideoId) return alert("Need Instructor/Course ID and Video ID");

    const url = await getVideoUrl(ownerId, uploadedVideoId);
    if (url) {
      setPlaybackUrl(url);
    }
  };

  return (
    <div className="container mt-5">
      <div className="card shadow-sm">
        <div className="card-header bg-primary">
          <h4 className="text-white mb-0">S3 Video Upload & Stream Tester</h4>
        </div>
        <div className="card-body">

          {/* Error Alert */}
          {error && <div className="alert alert-danger">{error}</div>}

          {/* Configs */}
          <div className="row mb-4">
            <div className="col">
              <label className="form-label">Instructor/Course ID</label>
              <input
                type="text"
                className="form-control"
                value={ownerId}
                onChange={e => setOwnerId(e.target.value)}
              />
            </div>
          </div>

          <hr />

          {/* Upload */}
          <h5>1. Upload Video</h5>
          <div className="input-group mb-3">
            <input
              type="file"
              className="form-control"
              accept="video/*"
              onChange={handleFileChange}
            />
            <button
              className="btn btn-success"
              onClick={handleUpload}
              disabled={loading || !selectedFile}
            >
              {loading ? "Processing..." : "Upload to S3"}
            </button>
          </div>

          {/* Progress Bar */}
          {uploadProgress > 0 && uploadProgress < 100 && (
            <div className="progress mb-3">
              <div
                className="progress-bar progress-bar-striped progress-bar-animated"
                role="progressbar"
                style={{ width: `${uploadProgress}%` }}
              >
                {uploadProgress}%
              </div>
            </div>
          )}

          {uploadedVideoId && (
            <div className="alert alert-success">
              <strong>Success!</strong> Video ID: <code>{uploadedVideoId}</code>
            </div>
          )}

          <hr />

          {/* VIEW SECTION */}
          <h5>2. Test Playback</h5>
          <div className="input-group mb-3">
            <input
              type="text"
              className="form-control"
              placeholder="Video ID"
              value={uploadedVideoId}
              onChange={(e) => setUploadedVideoId(e.target.value)}
            />
            <button
              className="btn btn-primary"
              onClick={handleView}
              disabled={loading}
            >
              Get View URL
            </button>
          </div>

          {/* Video Player */}
          {playbackUrl && (
            <div className="mt-4">
              <h6>Video Preview:</h6>
              <div className="ratio ratio-16x9 bg-dark">
                <video controls autoPlay src={playbackUrl}>
                  Your browser does not support the video tag.
                </video>
              </div>
              <div className="mt-2" style={{ fontSize: '0.8rem' }}>
                <strong>Signed URL:</strong> {playbackUrl}
              </div>
            </div>
          )}

        </div>
      </div>
    </div>
  );
};

export default VideoTest;