package com.ihren.processor;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;

import java.util.Map;

//@Configuration
//@Testcontainers
public class KafkaContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, TestExecutionListener {

    DockerContainer<ConfluentKafkaContainer> kafkaContainer = new ConfluentKafkaDockerContainer();

    @Override
    public void beforeTestClass(TestContext testContext) {
        kafkaContainer.start();
    }

    @Override
    public void afterTestClass(TestContext testContext) {
        kafkaContainer.destroy();
    }

    @Override
    public void prepareTestInstance(TestContext testContext) {
        kafkaContainer.start();
    }

    @Override
    public void afterTestMethod(TestContext testContext) {
        kafkaContainer.destroy();
    }


    @Override
    public void initialize(ConfigurableApplicationContext context) {
        String bootstrapServers = kafkaContainer.getContainer().getBootstrapServers();
        var properties = Map.of(
                "spring.kafka.bootstrap-servers", bootstrapServers
        );

        TestPropertyValues.of(properties)
                .applyTo(context.getEnvironment());
    }
}
