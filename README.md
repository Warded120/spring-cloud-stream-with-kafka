🧪 To Do:

- Dead Letter Topic (DLT)
- Replay mechanism via HTTP request

✅ Acceptance Criteria:

- Implement DLTHandler to send broken messages to DLT
- Implement Replay mechanism
- Implement REST Controller to trigger manually replay mechanism
- Error code and message are added to headers
- Send messages with a custom header (isDLT or so) and check if the header exists after it goes to dlt topic
