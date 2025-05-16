package com.ihren.processor.container;

import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class ConfluentKafkaDockerContainer implements DockerContainer<ConfluentKafkaContainer> {
    private static final DockerImageName IMAGE =
            DockerImageName.parse("confluentinc/cp-kafka").withTag("7.4.0");


    private final ConfluentKafkaContainer container = new ConfluentKafkaContainer(IMAGE);

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
