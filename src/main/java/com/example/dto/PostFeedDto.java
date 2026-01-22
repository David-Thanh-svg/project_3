package com.example.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostFeedDto {
    private Long id;
    private String author;
    private String content;
    private List<PostMediaDto> media;
}

