package com.example.DocumentStorageSystem1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContentDto {
    private Long id;
    private String content;
    private Long doc_id;
}
