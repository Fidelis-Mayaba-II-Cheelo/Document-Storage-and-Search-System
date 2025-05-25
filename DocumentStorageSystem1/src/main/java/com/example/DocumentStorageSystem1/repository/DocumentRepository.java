package com.example.DocumentStorageSystem1.repository;

import com.example.DocumentStorageSystem1.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
