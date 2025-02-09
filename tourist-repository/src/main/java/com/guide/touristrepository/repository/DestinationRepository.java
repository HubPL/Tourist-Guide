package com.guide.touristrepository.repository;

import com.guide.touristmodel.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DestinationRepository extends JpaRepository<Destination, Long> {
    List<Destination> findByCityNameContainingIgnoreCase(String fragment);
    Destination findByCityName(String cityName);
    Destination findByCityNameIgnoreCase(String cityName);
}
