package com.ihren.processor;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;

import java.util.Map;

@Testcontainers
public class KafkaContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, TestExecutionListener {

    DockerContainer<ConfluentKafkaContainer> kafkaContainer = new ConfluentKafkaDockerContainer();

    @Override
    public void beforeTestClass(TestContext testContext) {
    }

    @Override
    public void afterTestClass(TestContext testContext) {
    }

    @Override
    public void prepareTestInstance(TestContext testContext) {
    }

    @Override
    public void afterTestMethod(TestContext testContext) {
    }


    @Override
    public void initialize(ConfigurableApplicationContext context) {
        var properties = Map.of(
                "spring.kafka.bootstrap-servers", kafkaContainer.getContainer().getBootstrapServers()
        );

        TestPropertyValues.of(properties)
                .applyTo(context.getEnvironment());
    }
}
