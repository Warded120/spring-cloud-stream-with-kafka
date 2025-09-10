# Spring Cloud Stream with Kafka
This document outlines the requir[README.md](README.md)ed tasks for understanding what is Dead Letter Topic (DLT) strategy and implementing replay mechanism.

🧪 To Do:

- Dead Letter Topic (DLT)
- Replay mechanism via HTTP request

✅ Acceptance Criteria:

- Implement DLTHandler to send broken messages to DLT
- Implement Replay mechanism
- Implement REST Controller to trigger manually replay mechanism
- Error code and message are added to headers
- Send messages with a custom header (isDLT or so) and check if the header exists after it goes to dlt topic
