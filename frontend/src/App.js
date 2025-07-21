import React, { useRef, useState } from "react";

function CameraUpload() {
  const videoRef = useRef(null);
  const canvasRef = useRef(null);
  const [imageCaptured, setImageCaptured] = useState(null);
  const [response, setResponse] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const startCamera = async () => {
    try {
      // Request back camera if available on mobile devices
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: { ideal: "environment" } },
      });
      videoRef.current.srcObject = stream;
    } catch (err) {
      console.error("Camera access denied:", err);
    }
  };

  const stopCamera = () => {
    const stream = videoRef.current?.srcObject;
    if (stream) {
      stream.getTracks().forEach((track) => track.stop());
      videoRef.current.srcObject = null;
    }
  };

  const capturePhoto = () => {
    const video = videoRef.current;
    const canvas = canvasRef.current;
    const context = canvas.getContext("2d");

    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    context.drawImage(video, 0, 0);

    canvas.toBlob((blob) => {
      setImageCaptured(blob);
    }, "image/jpeg");

    stopCamera();
  };

  const uploadPhoto = async () => {
    if (!imageCaptured) return;

    setIsLoading(true);
    setResponse("");

    const formData = new FormData();
    formData.append("image", imageCaptured, "photo.jpg");

    try {
      const res = await fetch("/api/scan", {
        method: "POST",
        body: formData,
      });

      const data = await res.text();
      setResponse(data);
    } catch (err) {
      console.error("Upload failed:", err);
      setResponse("Error uploading image.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex flex-col items-center gap-4 p-6">
      <h1 className="text-xl font-bold">📸 Scan Food with Camera</h1>

      <video
        ref={videoRef}
        autoPlay
        playsInline
        className="w-64 h-auto rounded shadow"
      />

      <canvas ref={canvasRef} style={{ display: "none" }} />

      <div className="flex gap-4">
        <button
          onClick={startCamera}
          className="bg-green-600 text-white px-4 py-2 rounded"
        >
          Start Camera
        </button>
        <button
          onClick={capturePhoto}
          className="bg-yellow-500 text-white px-4 py-2 rounded"
        >
          Capture Photo
        </button>
        <button
          onClick={uploadPhoto}
          className="bg-blue-600 text-white px-4 py-2 rounded"
          disabled={isLoading}
        >
          {isLoading ? "Uploading..." : "Upload"}
        </button>
      </div>

      {isLoading && (
        <div className="flex items-center gap-2 mt-4">
          <div className="w-6 h-6 border-4 border-blue-400 border-t-transparent rounded-full animate-spin"></div>
          <span className="text-blue-500 font-medium">Uploading image...</span>
        </div>
      )}

      {imageCaptured && (
        <div className="bg-white p-4 rounded shadow max-w-md mt-4">
          <h2 className="font-semibold">Preview:</h2>
          <img
            src={URL.createObjectURL(imageCaptured)}
            alt="Captured"
            className="w-64 h-auto rounded shadow mt-2"
          />
        </div>
      )}

      {response && !isLoading && (
        <div className="bg-white p-4 rounded shadow max-w-md mt-4">
          <h2 className="font-semibold">AI Response:</h2>
          <p className="whitespace-pre-wrap">{response}</p>
        </div>
      )}
    </div>
  );
}

export default CameraUpload;
