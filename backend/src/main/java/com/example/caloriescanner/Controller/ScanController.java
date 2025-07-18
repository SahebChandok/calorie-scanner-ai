package com.example.caloriescanner.Controller;

import com.example.caloriescanner.Model.ScannedImageUrl;
import com.example.caloriescanner.Repository.imageURLRepository;
import com.example.caloriescanner.Service.OpenAIService;
import com.example.caloriescanner.Service.S3Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/scan")
public class ScanController {
    private OpenAIService openaiservice;
    private S3Service s3Service;
    private imageURLRepository imageurlrepo;

    public ScanController(OpenAIService openaiservice, S3Service s3Service, imageURLRepository imageurlrepo){
        this.openaiservice = openaiservice;
        this.s3Service = s3Service;
        this.imageurlrepo = imageurlrepo;
    }
    @GetMapping("/hello")
    public String getFunction(){
        return "Hello From Get Method!";
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String scanImage(@RequestParam("image") MultipartFile image) throws Exception{
        byte[] imageBytes = image.getBytes();
        String imageUrl = s3Service.uploadFile(image);

        ScannedImageUrl url = new ScannedImageUrl(imageUrl);
        imageurlrepo.save(url);
        String summary = openaiservice.analyzeImage(imageBytes);
        return summary;
    }

}
