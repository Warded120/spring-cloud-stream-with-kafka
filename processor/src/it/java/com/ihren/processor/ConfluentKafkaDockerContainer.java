package com.ihren.processor;

import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class ConfluentKafkaDockerContainer implements DockerContainer<ConfluentKafkaContainer> {
    private static final DockerImageName IMAGE =
            DockerImageName.parse("confluentinc/cp-kafka").withTag("7.4.0");


    ConfluentKafkaContainer container = new ConfluentKafkaContainer(IMAGE);

    @Override
    public void start() {
        container.start();
    }

    @Override
    public void destroy() {
        container.stop();
    }

    @Override
    public ConfluentKafkaContainer getContainer() {
        return container;
    }
}
