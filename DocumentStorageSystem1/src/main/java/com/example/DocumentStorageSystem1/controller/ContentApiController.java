package com.example.DocumentStorageSystem1.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.DocumentStorageSystem1.dto.ContentDto;
import com.example.DocumentStorageSystem1.service.impl.ContentServiceImpl;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class ContentApiController {

    private final ContentServiceImpl contentService;

    // Get content by contentId and documentId
    @GetMapping("/view_content")
    public ResponseEntity<ContentDto> viewContent(@RequestParam Long contentId, @RequestParam Long documentId) {
        try {
            ContentDto content = contentService.getSingleContent(contentId, documentId);
            return ResponseEntity.ok(content);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get all content for all documents
    @GetMapping("/view_contents")
    public ResponseEntity<List<ContentDto>> viewAllContent() {
        try {
            List<ContentDto> contentDtoList = contentService.getDocumentContents();
            return ResponseEntity.ok(contentDtoList);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Add new content
    @PostMapping("/add_content")
    public ResponseEntity<ContentDto> addContent(@RequestBody ContentDto contentDto) {
        try {
            ContentDto savedContent = contentService.addContentToDocuments(contentDto);
            return ResponseEntity.ok(savedContent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update existing content
    @PutMapping("/update_content")
    public ResponseEntity<ContentDto> updateContent(@RequestParam Long contentId,
                                                    @RequestBody ContentDto updatedContentDto) {
        try {
            ContentDto updatedContent = contentService.updateDocumentsContent(contentId, updatedContentDto);
            return ResponseEntity.ok(updatedContent);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

