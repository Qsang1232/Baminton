package com.example.demo.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long courtId;
    private int rating;
    private String comment;
}