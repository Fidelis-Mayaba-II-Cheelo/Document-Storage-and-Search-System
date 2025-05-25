package com.example.DocumentStorageSystem1.entity;


import java.time.LocalDate;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doc_id;

    @Column(name = "author")
    private String author;

    @Column(name = "title")
    private String title;

    @Column(name = "upload_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate upload_date;

    private String fileName;
    private String fileType;

    @Lob
    private byte[] data;

}
