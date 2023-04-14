package com.example.aggregatormongo.message_storage.repository;

import com.example.aggregatormongo.message_storage.documents.Processing;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProcessingRepository extends MongoRepository<Processing, String> {

    List<Processing> findAllByTransactionId(String transactionId);
}
