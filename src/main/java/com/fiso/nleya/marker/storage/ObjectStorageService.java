package com.fiso.nleya.marker.storage;

import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class ObjectStorageService {

    @Value("${minio.bucket}")
    String bucketName;

    @Value("${minio.url}")
    String minioUrl;

    private final MinioClient minioClient;


    private void initBucket() {
        try {

            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

        } catch (Exception e) {
            log.info("Error occurred in initializing bucket: {}", e.getMessage());
        }
    }


    public String uploadFile(MultipartFile file, String username) {
        initBucket();

        String objectName = String.format("/ms/%s/%s", username, file.getOriginalFilename());
        try {

            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                                    file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

        } catch (Exception e) {
            log.error("Error occurred: {}", e.getMessage());
        }
        log.info("Successfully uploaded file '{}'", objectName);
        return objectName;
    }


    public void deleteObjects(List<String> objectNames) {

        initBucket();
        List<DeleteObject> objects = objectNames.stream().map(DeleteObject::new).toList();
        Iterable<Result<DeleteError>> results =
                minioClient.removeObjects(
                        RemoveObjectsArgs.builder().bucket(bucketName).objects(objects).build());
        for (Result<DeleteError> result : results) {
            try {
                DeleteError error = result.get();
                log.info("Error in deleting object {} : {}", error.objectName(), error.message());
            } catch (Exception e) {
                log.error("Error occurred in deleting object: {}", e.getMessage());
            }

        }
        log.info("Successfully deleted objects : {}", objectNames);


    }


    public void downloadObject(String objectName, String fileName) {
        initBucket();
        try {
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .filename(fileName)
                            .build());

        } catch (Exception ex) {
            log.error("Error occurred in downloading object: {}", ex.getMessage());
        }

    }


}