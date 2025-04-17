# Spring Cloud Stream with Kafka
This document outlines the required tasks for understanding and implementing a basic Spring Cloud Stream application integrated with Kafka.

## Kafka

📘 To Study:

- Kafka architecture and fundamentals
- Comparison with other message brokers
- Message delivery semantics (at-most-once, at-least-once, exactly-once)
- Partitions and their role in scalability
- Offset management and start offset strategies
- Concept and role of consumer groups

🧪 To Do:

- Set up a local Kafka and Zookeeper environment (Docker/Docker Compose is possible as well)
- Create Kafka topics
- Purge messages from a topic
- Add partitions to a topic
- Remove partitions from a topic
- Produce messages to a topic
- Consume messages from a topic
- Consume messages from the beginning of a topic

⚠️ Be prepared to explain these topics and demonstrate your knowledge.

## Spring Cloud Stream

Develop a basic processor that consumes messages from an upstream Kafka topic and publishes them to a downstream topic.

✅ Acceptance Criteria:

- Kafka binder is configured in the application
- Custom JSON serializer and deserializer are implemented and reused
- Jackson library is used with:
  - SerializationFeature.FAIL_ON_EMPTY_BEANS enabled 
  - DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES enabled
- A model class is created with the following fields:
  - id 
  - firstname 
  - lastname
- Kafka CLI or command examples for producing/consuming messages are included in the README.md 
- If Kafka is run in Docker, provide a corresponding Dockerfile or docker-compose.yml


