package com.example.DocumentStorageSystem1.repository;

import com.example.DocumentStorageSystem1.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {

    @Query("SELECT c FROM Content c WHERE c.document.doc_id = :docId")
    List<Content> findByDocumentDocId(@Param("docId") Long docId);
}
