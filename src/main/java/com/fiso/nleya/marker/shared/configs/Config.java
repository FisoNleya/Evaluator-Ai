package com.fiso.nleya.marker.shared.configs;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
@RequiredArgsConstructor
public class Config {


    @Value("${minio.access.key}")
    String accessKey;

    @Value("${minio.secret.key}")
    String secretKey;

    @Value("${minio.url}")
    String minioUrl;


    @Bean
    public TokenTextSplitter tokenTextSplitter() {
        return new TokenTextSplitter();
    }

    @Bean
    public MinioClient minioClient()  {
      return  MinioClient.builder()
                        .endpoint(minioUrl)
                        .credentials(accessKey, secretKey)
                        .build();
    }


}
