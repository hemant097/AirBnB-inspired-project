package com.example.project.airbnbapp.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter @Setter
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "unique_hotel_room_date",
                columnNames = {"hotel_id","room_id","date"}
))
@Builder
@AllArgsConstructor @NoArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer bookedCount;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    //this count shows the rooms which are held for a particular booking, for 10 minutes to complete payment
    private Integer reservedCount;

    private Integer totalCount;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal surgeFactor;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // price of inventory on that particular date

    @Column(nullable = false) //better to also include city here, as we are going to search with city, will avoid join queries
    private String city;

    @Column(nullable = false)
    private Boolean closed;


}
