package com.learning.awspring.common;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.learning.awspring.utils.AppConstants;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class LocalStackConfig {

    public static final String TEST_ACCESS_KEY = "test";
    public static final String TEST_SECRET_KEY = "test";

    public static final AWSCredentials TEST_CREDENTIALS =
            new BasicAWSCredentials(TEST_ACCESS_KEY, TEST_SECRET_KEY);

    static LocalStackContainer localStackContainer;

    static {
        System.setProperty("com.amazonaws.sdk.disableCbor", "true");
        localStackContainer =
                new LocalStackContainer(DockerImageName.parse("localstack/localstack"))
                        .withServices(S3, SQS)
                        .withExposedPorts(4566);
        localStackContainer.start();
        createQueue();
    }

    // Dirty Hack to create Queue if not exists for SQSListener
    private static void createQueue() {
        CreateQueueRequest createQueueRequest =
                new CreateQueueRequest(AppConstants.QUEUE)
                        .addAttributesEntry("MessageRetentionPeriod", "86400");

        try {
            amazonSQSAsync().createQueue(createQueueRequest);
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
        }
    }

    private static AmazonSQSAsync amazonSQSAsync() {
        return AmazonSQSAsyncClientBuilder.standard()
                .withEndpointConfiguration(localStackContainer.getEndpointConfiguration(SQS))
                .withCredentials(getCredentialsProvider())
                .build();
    }

    @Bean
    @Primary
    public AmazonS3 localstackAmazonS3() {
        return AmazonS3ClientBuilder.standard()
                .enablePathStyleAccess()
                .withEndpointConfiguration(localStackContainer.getEndpointConfiguration(SQS))
                .withCredentials(getCredentialsProvider())
                .build();
    }

    @Bean
    @Primary
    public AmazonSQSAsync localstackAmazonSQSAsync() {
        return amazonSQSAsync();
    }

    private static AWSCredentialsProvider getCredentialsProvider() {
        return new AWSStaticCredentialsProvider(TEST_CREDENTIALS);
    }
}
