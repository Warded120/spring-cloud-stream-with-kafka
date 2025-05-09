package com.ihren.processor.sink;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(OutputCaptureExtension.class)
class LoggingSinkTest {
    LoggingSink loggingSink = new LoggingSink();

    @Test
    void should_log_message(CapturedOutput output) {
        //when
        loggingSink.apply("Error occurred");

        //then
        assertTrue(output.getOut().contains("Error occurred"));
    }
}