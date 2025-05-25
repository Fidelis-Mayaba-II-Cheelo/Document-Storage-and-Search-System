// ContentServiceImpl.java
package com.example.DocumentStorageSystem1.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.DocumentStorageSystem1.dto.ContentDto;
import com.example.DocumentStorageSystem1.entity.Content;
import com.example.DocumentStorageSystem1.mapper.ContentMapper;
import com.example.DocumentStorageSystem1.repository.ContentRepository;
import com.example.DocumentStorageSystem1.repository.DocumentRepository;
import com.example.DocumentStorageSystem1.service.ContentService;

@Service
public class ContentServiceImpl implements ContentService {

    private final ContentRepository contentRepository;
    private final DocumentRepository documentRepository;
    private final ContentMapper contentMapper;

    
    public ContentServiceImpl(ContentRepository contentRepository,
                              DocumentRepository documentRepository,
                              ContentMapper contentMapper) {
        this.contentRepository = contentRepository;
        this.documentRepository = documentRepository;
        this.contentMapper = contentMapper;
    }

    @Override
    public ContentDto addContentToDocuments(ContentDto contentDto) {
        Content content = contentMapper.mapToContentEntity(contentDto);
        Content savedContent = contentRepository.save(content);
        return contentMapper.mapToContentDto(savedContent);
    }

    @Override
    public ContentDto getSingleContent(Long id, Long doc_id) {
        Optional<Content> content = contentRepository.findById(id);
        return content.map(contentMapper::mapToContentDto)
                .orElseThrow(() -> new RuntimeException("Content not found with ID: " + id));
    }

    @Override
    public List<ContentDto> getDocumentContents() {
        List<Content> contents = contentRepository.findAll();
        if (contents.isEmpty()) {
            throw new RuntimeException("No contents found for the documents");
        }
        List<ContentDto> contentsDtoList = new ArrayList<>();
        for (Content content : contents) {
            contentsDtoList.add(contentMapper.mapToContentDto(content));
        }
        return contentsDtoList;
    }

    @Override
    public ContentDto updateDocumentsContent(Long id, ContentDto contentDto) {
        Optional<Content> optionalContent = contentRepository.findById(id);
        if (optionalContent.isPresent()) {
            Content existingContent = optionalContent.get();
            Optional.ofNullable(contentDto.getContent()).ifPresent(existingContent::setContent);
            Content updatedContent = contentRepository.save(existingContent);
            return contentMapper.mapToContentDto(updatedContent);
        } else {
            throw new RuntimeException("Document content not found with ID: " + id);
        }
    }

    @Override
    public void deleteSingleContentForDocument(Long id, Long doc_id) {
        Optional<Content> content = contentRepository.findById(id);
        if (content.isPresent() && content.get().getDocument().getDoc_id().equals(doc_id)) {
            contentRepository.delete(content.get());
        } else {
            throw new RuntimeException("Deletion Failed");
        }
    }

    @Override
    public void deleteAllContentForDocument(Long doc_id) {
        List<Content> contents = contentRepository.findByDocumentDocId(doc_id);
        if (!contents.isEmpty()) {
            contentRepository.deleteAll(contents);
        } else {
            throw new RuntimeException("Deletion of all documents content failed");
        }
    }
}