package com.example.caloriescanner.Repository;

import com.example.caloriescanner.Model.ScannedImageUrl;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface imageURLRepository extends MongoRepository<ScannedImageUrl, String> {

}
