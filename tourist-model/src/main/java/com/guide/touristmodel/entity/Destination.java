package com.guide.touristmodel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "destinations")
@Getter
@Setter
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cityName;
    private String country;
    private String bookingIdBase64;
    private long ufi;
    private int productCount;

    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL)
    private List<Attraction> attractions = new ArrayList<>();

}