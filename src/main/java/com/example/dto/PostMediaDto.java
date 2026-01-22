package com.example.dto;

import com.example.entity.enums.MediaType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostMediaDto {
    private String url;
    private MediaType type;
}

