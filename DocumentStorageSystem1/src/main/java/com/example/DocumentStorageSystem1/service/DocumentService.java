package com.example.DocumentStorageSystem1.service;

import com.example.DocumentStorageSystem1.dto.DocumentDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface DocumentService {
    DocumentDto addDocuments(DocumentDto documentDto);
    DocumentDto getSingleDocument(Long doc_id);
    List<DocumentDto> getDocuments();
    DocumentDto updateDocuments(Long doc_id, DocumentDto documentDto);
    void deleteSingleDocument(Long doc_id);
    void deleteAllDocuments();

    void saveDocumentWithFile(DocumentDto dto, MultipartFile file) throws IOException;

    Optional<DocumentDto> getDocument(Long id);

}
