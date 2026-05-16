package com.Jarvis.Jarvis.service;

import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.springframework.stereotype.Service;

@Service
public class WebcamService {

    private final WebClient webClient = WebClient.create("http://localhost:5000");

    private VideoCapture camera;
    private CascadeClassifier faceDetector;
    private int detectedFaceCount = 0;


     public WebcamService() {
        nu.pattern.OpenCV.loadLocally();
        loadCascade();
    }

    public void loadCascade(){
        try {
            InputStream stream = getClass()
                 .getResourceAsStream("/haarcascades/haarcascade_frontalface_default.xml");

            File temp = File.createTempFile("haarcascade", ".xml");
            temp.deleteOnExit();
            Files.copy(stream, temp.toPath() , StandardCopyOption.REPLACE_EXISTING);


            faceDetector = new CascadeClassifier(temp.getAbsolutePath());
            System.out.println("Haar Cascade Loaded successfully!");
        } catch (Exception e) {
            System.out.println("Failed to load cascade: " + e.getMessage());
        }
    }
    
    public void openCamera(){
        camera = new VideoCapture(0);
        if (camera.isOpened()) {
            System.out.println("Webcam opened successfully!");
        }else{
            System.out.println("Failed to open Webcam !");
        }
    }


    public String captureAndDetect(){
        Mat frame = new Mat();
        camera.read(frame);

        if (frame.empty()) return "";

        //Convert to grayScale for detection
        Mat gray = new Mat();
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(gray, gray);


        // detect face
        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(gray, faces , 1.1 , 5 , 0,
             new Size(80,80) , new Size()
        );

        Rect[] faceArray = faces.toArray();
        detectedFaceCount = faceArray.length;

     
        //Draw rect around each face
        for (Rect face : faceArray) {
            Imgproc.rectangle(frame, face, new Scalar(0,255,0),2);
        }

        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".jpg", frame, buffer);
        return Base64.getEncoder().encodeToString(buffer.toArray());
    }

    public int getDetectedFaceCount(){
        return detectedFaceCount;
    }

    public boolean isCameraOpen(){
        return camera != null && camera.isOpened();
    }


    public String detectEmotion(String base64Image){
        try{
            Map<String, Object> response = webClient.post()
                .uri("/detect-emotion")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("image", base64Image))
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

                if (response != null && response.containsKey("emotion")) {
                    return (String) response.get("emotion");
                }
                return "neutral";
            
        }catch(Exception e){
            System.out.println("Emotion API error: " + e.getMessage());
            return "unknow";
        }
    }

    public void activateCamera(){
        if (camera == null || !camera.isOpened()) {
            openCamera();
            System.out.println("✅ Camera activated by user!");
        }
    }

    public void deactivateCamera(){
        if (camera != null && camera.isOpened()) {
            camera.release();
            camera = null;
            System.out.println("📷 Camera deactivated!");
        }
    }

    
}
