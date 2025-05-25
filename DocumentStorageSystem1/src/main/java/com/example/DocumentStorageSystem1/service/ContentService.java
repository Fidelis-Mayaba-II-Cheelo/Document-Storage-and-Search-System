package com.example.DocumentStorageSystem1.service;

import com.example.DocumentStorageSystem1.dto.ContentDto;

import java.util.List;

public interface ContentService {
    ContentDto addContentToDocuments(ContentDto contentDto);
    ContentDto getSingleContent(Long id, Long doc_id);
    List<ContentDto> getDocumentContents();
    ContentDto updateDocumentsContent(Long id, ContentDto contentDto);
    void deleteSingleContentForDocument(Long id, Long doc_id);
    void deleteAllContentForDocument(Long doc_id);
}
