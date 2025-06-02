# Spring Cloud Stream with Kafka
This document outlines the required tasks for understanding what is Dead Letter Topic (DLT) strategy and implementing replay mechanism.

🧪 To Do:

- Dead Letter Topic (DLT)
- Replay mechanism via HTTP request

✅ Acceptance Criteria:

- Implement DLTHandler to send broken messages to DLT
- Implement Replay mechanism
- Implement REST Controller to trigger manually replay mechanism
- Error code and message are added to headers
