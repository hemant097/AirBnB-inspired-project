package com.example.project.airbnbapp.Entity;

import com.example.project.airbnbapp.Entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter @Setter
@Entity
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "=user_id")
    private User user;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Integer age;

//    @ManyToMany(mappedBy = "guests") this behaviour is not required, thus commented out
//    private Set<Booking> bookings;



}
