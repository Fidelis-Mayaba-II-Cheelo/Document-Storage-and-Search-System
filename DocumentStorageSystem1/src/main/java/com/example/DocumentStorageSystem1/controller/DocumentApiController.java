package com.example.DocumentStorageSystem1.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import com.example.DocumentStorageSystem1.entity.Document;
import com.example.DocumentStorageSystem1.repository.DocumentRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.DocumentStorageSystem1.dto.DocumentDto;
import com.example.DocumentStorageSystem1.service.impl.DocumentServiceImpl;

import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class DocumentApiController {
    private final DocumentServiceImpl documentService;
    private DocumentRepository documentRepository;

    //Method for getting one Document
    @GetMapping("/view_document")
    public ResponseEntity<DocumentDto> viewDocument(@RequestParam Long documentId){
        try {
            DocumentDto document = documentService.getSingleDocument(documentId); // No tasksDto parameter needed
            return ResponseEntity.ok(document);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    //Method for getting all Documents
    @GetMapping("/view_documents")
    public ResponseEntity<List<DocumentDto>> viewAllDocuments(Model model){
        try{
            List<DocumentDto> documentDto = documentService.getDocuments();
            return ResponseEntity.ok(documentDto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/documents/{documentId}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long documentId) {
        Optional<Document> documentOpt = documentRepository.findById(documentId);
        if (documentOpt.isPresent()) {
            Document doc = documentOpt.get();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=\"" + doc.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(doc.getData());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // Add a new document
    @PostMapping("/add_document")
    public ResponseEntity<DocumentDto> addDocument(@RequestBody DocumentDto documentDto) {
        try {
            DocumentDto savedDoc = documentService.addDocuments(documentDto);
            return ResponseEntity.ok(savedDoc);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update an existing document
    @PutMapping("/update_document")
    public ResponseEntity<DocumentDto> updateDocument(@RequestParam Long documentId,
                                                      @RequestBody DocumentDto updatedDocumentDto) {
        try {
            DocumentDto updated = documentService.updateDocuments(documentId, updatedDocumentDto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
