package com.example.DocumentStorageSystem1.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDto {
    private Long doc_id;
    private String author;
    private String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate upload_date;

    private String fileName;
    private String fileType;
    private byte[] data;
    


}
