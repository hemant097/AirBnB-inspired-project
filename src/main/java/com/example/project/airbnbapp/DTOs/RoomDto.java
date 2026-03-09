package com.example.project.airbnbapp.DTOs;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //as Room has fetchType LAZY for Hotel , thus not required here

    private String type;

    private BigDecimal basePrice;

    private String[] photos;

    private String[] amenities;

    private Integer totalCount;

    private Integer capacity;

}
