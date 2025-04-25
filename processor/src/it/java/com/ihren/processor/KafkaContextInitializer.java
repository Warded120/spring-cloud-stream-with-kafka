package com.ihren.processor;

import com.ihren.processor.container.ConfluentKafkaDockerContainer;
import com.ihren.processor.container.DockerContainer;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.testcontainers.kafka.ConfluentKafkaContainer;

import java.util.Map;
import java.util.Optional;

public class KafkaContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, TestExecutionListener {

    private final DockerContainer<ConfluentKafkaContainer> kafkaDockerContainer = new ConfluentKafkaDockerContainer();
    private static ConfluentKafkaContainer kafkaContainer;

    @Override
    public void beforeTestClass(TestContext testContext) {
        if(DirtiesContext.ClassMode.BEFORE_CLASS == getTestIsolationLevel(testContext.getTestClass())) {
            kafkaDockerContainer.start();
            kafkaContainer = kafkaDockerContainer.getContainer();
        }
    }

    @Override
    public void afterTestClass(TestContext testContext) {
        if(DirtiesContext.ClassMode.BEFORE_CLASS == getTestIsolationLevel(testContext.getTestClass())) {
            kafkaDockerContainer.destroy();
        }
    }

    @Override
    public void prepareTestInstance(TestContext testContext) {
        if(DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD == getTestIsolationLevel(testContext.getTestClass())) {
            kafkaDockerContainer.start();
            kafkaContainer = kafkaDockerContainer.getContainer();
        }
    }

    @Override
    public void afterTestMethod(TestContext testContext) {
        if(DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD == getTestIsolationLevel(testContext.getTestClass())) {
            kafkaDockerContainer.destroy();
        }
    }


    @Override
    public void initialize(ConfigurableApplicationContext context) {
        var properties = Map.of(
                "spring.kafka.bootstrap-servers", kafkaContainer.getBootstrapServers(),
                "spring.kafka.consumer.group-id", "consumer"
        );

        TestPropertyValues.of(properties)
                .applyTo(context.getEnvironment());
    }

    private Object getTestIsolationLevel(Class<?> clazz) {
        final var annotationAttributes = AnnotatedElementUtils.getMergedAnnotationAttributes(clazz, DirtiesContext.class);

        return Optional.ofNullable(annotationAttributes)
                .map(attributes -> attributes.get("classMode"))
                .orElse(null);
    }
}
