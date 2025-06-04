package com.ihren.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.listener.RetryListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyRetryListener implements RetryListener {
    @Override
    public void failedDelivery(ConsumerRecord<?, ?> record, Exception ex, int deliveryAttempt) {
        log.error("failed delivery #" + deliveryAttempt + ": " + ex.getMessage());
    }

    @Override
    public void recovered(ConsumerRecord<?, ?> record, Exception ex) {
        log.info("recovered record: " + ex.getMessage());
    }

    @Override
    public void recoveryFailed(ConsumerRecord<?, ?> record, Exception original, Exception failure) {
        log.error("recoveryFailed with failure: " + failure.getMessage());
    }

    @Override
    public void failedDelivery(ConsumerRecords<?, ?> records, Exception ex, int deliveryAttempt) {
        log.error("failed delivery for records #" + deliveryAttempt + ": " + ex.getMessage());
    }

    @Override
    public void recovered(ConsumerRecords<?, ?> records, Exception ex) {
        log.info("recovered records: " + ex.getMessage());
    }

    @Override
    public void recoveryFailed(ConsumerRecords<?, ?> records, Exception original, Exception failure) {
        log.error("recoveryFailed for records: " + failure.getMessage());
    }
}
