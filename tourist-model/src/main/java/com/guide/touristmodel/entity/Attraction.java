package com.guide.touristmodel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attractions")
@Getter
@Setter
public class Attraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookingProductId;
    private String name;
    private String slug;
    @Column(length = 2000)
    private String description;
    private double price;
    private String currency;
    private double reviewsAverage;
    private int reviewsTotal;
    private String photoUrl;
    private double lat;
    private double lng;

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Destination destination;

    @OneToMany(mappedBy = "attraction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public void removeReview(Review review) {
        reviews.remove(review);
        review.setAttraction(null);
    }

    public void addReview(Review review) {
        reviews.add(review);
        review.setAttraction(this);
    }
}