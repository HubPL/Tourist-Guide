package com.guide.touristrepository.repository;

import com.guide.touristmodel.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}