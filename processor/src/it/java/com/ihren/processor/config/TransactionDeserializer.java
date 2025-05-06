package com.ihren.processor.config;

import com.ihren.processor.model.Transaction;
import com.ihren.processor.serialization.GenericDeserializer;

public class TransactionDeserializer extends GenericDeserializer<Transaction> {
    public TransactionDeserializer() {
        super(Transaction.class);
    }
}
