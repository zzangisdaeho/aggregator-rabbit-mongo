package com.example.aggregatormongo.message_storage.repository;

import com.example.aggregatormongo.message_storage.documents.FailLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FailLogRepository extends MongoRepository<FailLog, String> {

    Optional<FailLog> findByTransactionId(String transactionId);
}
