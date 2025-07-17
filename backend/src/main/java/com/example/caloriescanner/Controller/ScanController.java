package com.example.caloriescanner.Controller;

import com.example.caloriescanner.Service.OpenAIService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/scan")
public class ScanController {
    private OpenAIService openaiservice;

    public ScanController(OpenAIService openaiservice){
        this.openaiservice = openaiservice;
    }
    @GetMapping("/hello")
    public String getFunction(){
        return "Hello From Get Method!";
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String scanImage(@RequestParam("image") MultipartFile image) throws Exception{
        byte[] imageBytes = image.getBytes();
        String summary = openaiservice.analyzeImage(imageBytes);
        return summary;
    }


}
