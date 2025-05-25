// ContentMapper.java
package com.example.DocumentStorageSystem1.mapper;

import org.springframework.stereotype.Component;

import com.example.DocumentStorageSystem1.dto.ContentDto;
import com.example.DocumentStorageSystem1.entity.Content;
import com.example.DocumentStorageSystem1.entity.Document;
import com.example.DocumentStorageSystem1.repository.DocumentRepository;

@Component
public class ContentMapper {

    private final DocumentRepository documentRepository;

    //@Autowired
    public ContentMapper(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Content mapToContentEntity(ContentDto contentDto) {
        Content content = new Content();
        content.setContent(contentDto.getContent());

        Document document = documentRepository.findById(contentDto.getDoc_id())
                .orElseThrow(() -> new RuntimeException("Document not found with ID: " + contentDto.getDoc_id()));
        content.setDocument(document);

        if (contentDto.getId() != null) {
            content.setId(contentDto.getId());
        }

        return content;
    }

    public ContentDto mapToContentDto(Content content) {
        ContentDto contentDto = new ContentDto();
        contentDto.setId(content.getId());
        contentDto.setContent(content.getContent());
        contentDto.setDoc_id(content.getDocument().getDoc_id());
        return contentDto;
    }
}
