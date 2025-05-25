package com.example.DocumentStorageSystem1.mapper;

import com.example.DocumentStorageSystem1.dto.DocumentDto;
import com.example.DocumentStorageSystem1.entity.Document;

public class DocumentMapper {

    public static Document mapToDocumentEntity(DocumentDto documentDto){
        Document document = new Document();
        document.setAuthor(documentDto.getAuthor());
        document.setTitle(documentDto.getTitle());
        document.setUpload_date(documentDto.getUpload_date());
        document.setFileName(documentDto.getFileName());
        document.setFileType(documentDto.getFileType());
        document.setData(documentDto.getData());

        if(documentDto.getDoc_id() != null){
            document.setDoc_id(documentDto.getDoc_id());
        }
        return document;
    }

    public static DocumentDto mapToDocumentDto(Document document){
        DocumentDto documentDto = new DocumentDto();
        documentDto.setDoc_id(document.getDoc_id());
        documentDto.setAuthor(document.getAuthor());
        documentDto.setTitle(document.getTitle());
        documentDto.setUpload_date(document.getUpload_date());
        documentDto.setFileName(document.getFileName());
        documentDto.setFileType(document.getFileType());
        documentDto.setData(document.getData());
        return documentDto;
    }
}
