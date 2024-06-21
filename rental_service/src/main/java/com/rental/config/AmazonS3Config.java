package com.rental.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;


@Configuration
public class AmazonS3Config {

	@Value("${AWS_S3_ACCESS_KEY_ID}")
    private String accessKey;

    @Value("${AWS_S3_SECRET_ACCESS_KEY}")
    private String secretKey;

    @Value("${AWS_S3_REGION}")
    private String region;


    /**
     * Creates and configures an instance of the AmazonS3 client.
     * This client is used to interact with the Amazon S3 service.
     *
     * @return The configured AmazonS3 client instance.
     */
    @Bean
    public AmazonS3 amazonS3Client() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(region)
                .build();
    }

}

