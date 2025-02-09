package com.guide.touristrepository.repository;

import com.guide.touristmodel.entity.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
<<<<<<< HEAD
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
=======
>>>>>>> parent of c0e0394 (zepsute testy)

public interface AttractionRepository extends JpaRepository<Attraction, Long> {
    @Query("SELECT a FROM Attraction a WHERE a.bookingProductId = :bookingProdId AND a.destination.id = :destId")
    Attraction findByBookingProductIdAndDestinationId(
            @Param("bookingProdId") String bookingProdId,
            @Param("destId") Long destId
    );
}