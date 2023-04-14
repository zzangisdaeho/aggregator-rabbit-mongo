package com.example.aggregatormongo.message_storage.repository;

import com.example.aggregatormongo.message_storage.documents.SuccessLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SuccessLogRepository extends MongoRepository<SuccessLog, String> {

    Optional<SuccessLog> findByTransactionId(String transactionId);
}
