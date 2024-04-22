package com.learning.aws.spring;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.KINESIS;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;

@TestConfiguration(proxyBeanMethods = false)
public class TestKinesisProducerApplication {

    @Bean
    LocalStackContainer localStackContainer(DynamicPropertyRegistry dynamicPropertyRegistry) {
        LocalStackContainer localStackContainer =
                new LocalStackContainer(
                        DockerImageName.parse("localstack/localstack").withTag("3.3.0"));
        dynamicPropertyRegistry.add("spring.cloud.aws.endpoint", localStackContainer::getEndpoint);
        dynamicPropertyRegistry.add(
                "spring.cloud.aws.region.static", localStackContainer::getRegion);
        dynamicPropertyRegistry.add(
                "spring.cloud.aws.access-key", localStackContainer::getAccessKey);
        dynamicPropertyRegistry.add(
                "spring.cloud.aws.secret-key", localStackContainer::getSecretKey);
        return localStackContainer;
    }

    @Bean
    KinesisAsyncClient amazonKinesis(LocalStackContainer localStackContainer) {
        return KinesisAsyncClient.builder()
                .endpointOverride(localStackContainer.getEndpointOverride(KINESIS))
                .region(Region.of(localStackContainer.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        localStackContainer.getAccessKey(),
                                        localStackContainer.getSecretKey())))
                .build();
    }

    @Bean
    public AtomicReference<Message<List<Message<?>>>> messageHolder() {
        return new AtomicReference<>();
    }

    @Bean
    public CountDownLatch messageBarrier() {
        return new CountDownLatch(1);
    }

    @Bean
    public Consumer<Message<List<Message<?>>>> eventConsumerBatchProcessingWithHeaders() {
        return eventMessages -> {
            messageHolder().set(eventMessages);
            messageBarrier().countDown();
        };
    }

    public static void main(String[] args) {
        SpringApplication.from(KinesisProducerApplication::main)
                .with(TestKinesisProducerApplication.class)
                .run(args);
    }
}
