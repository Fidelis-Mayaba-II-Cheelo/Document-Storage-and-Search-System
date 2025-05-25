package com.example.DocumentStorageSystem1.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.DocumentStorageSystem1.dto.DocumentDto;
import com.example.DocumentStorageSystem1.entity.Document;
import com.example.DocumentStorageSystem1.mapper.DocumentMapper;
import com.example.DocumentStorageSystem1.repository.DocumentRepository;
import com.example.DocumentStorageSystem1.service.DocumentService;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository documentRepository;


    @Override
    public DocumentDto addDocuments(DocumentDto documentDto){
        Document document = DocumentMapper.mapToDocumentEntity(documentDto);

        Document savedDocument = documentRepository.save(document);

        return DocumentMapper.mapToDocumentDto(savedDocument);
    }

    @Override
    public void saveDocumentWithFile(DocumentDto dto, MultipartFile file) throws IOException {
        Document document = DocumentMapper.mapToDocumentEntity(dto);
        document.setFileName(file.getOriginalFilename());
        document.setFileType(file.getContentType());
        document.setData(file.getBytes());
        documentRepository.save(document);
    }


    @Override
    public Optional<DocumentDto> getDocument(Long id) {
        return documentRepository.findById(id).map(DocumentMapper::mapToDocumentDto);
    }

    @Override
    public DocumentDto getSingleDocument(Long doc_id){
        Optional<Document> document = documentRepository.findById(doc_id);

        if(document.isPresent()){
            Document wantedDocument = document.get();
            return DocumentMapper.mapToDocumentDto(wantedDocument);
        } else{
            throw new RuntimeException("Document not found with ID: " + doc_id);
        }
    }

    @Override
    public List<DocumentDto> getDocuments(){
        List<Document> documents = documentRepository.findAll();
        List<DocumentDto> documentsDtoList = new ArrayList<>();

        if(!documents.isEmpty()){
            for(Document document: documents){
                DocumentDto documentDto = DocumentMapper.mapToDocumentDto(document);
                documentsDtoList.add(documentDto);
            }
            return documentsDtoList;
        } else{
            throw new RuntimeException("No documents found");
        }
    }

    @Override
    public DocumentDto updateDocuments(Long doc_id, DocumentDto documentDto){
        Optional<Document> optionalDocument = documentRepository.findById(doc_id);

        if (optionalDocument.isPresent()) {
            Document existingDocument = optionalDocument.get();

            Optional.ofNullable(documentDto.getAuthor()).ifPresent(existingDocument::setAuthor);
            Optional.ofNullable(documentDto.getTitle()).ifPresent(existingDocument::setTitle);
            Optional.ofNullable(documentDto.getUpload_date()).ifPresent(existingDocument::setUpload_date);

            // ... update other fields similarly ...

            Document updatedDocument = documentRepository.save(existingDocument); // Save the existingTask!
            return DocumentMapper.mapToDocumentDto(updatedDocument);
        } else {
            throw new RuntimeException("Document not found with ID: " + doc_id);
        }
    }

    @Override
    public void deleteSingleDocument(Long doc_id){
        Optional<Document> document = documentRepository.findById(doc_id);
        if(document.isPresent()){
            documentRepository.delete(document.get());
        } else{
            throw new RuntimeException("Deletion Failed");
        }
    }

    @Override
    public void deleteAllDocuments(){
        List<Document> documents = documentRepository.findAll();
        if(!documents.isEmpty()){
            documentRepository.deleteAll(documents);
        } else{
            throw new RuntimeException("Deletion of all documents failed");
        }
    }

}
