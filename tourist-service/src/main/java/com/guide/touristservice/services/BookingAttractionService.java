package com.guide.touristservice.services;

import com.guide.touristmodel.entity.Attraction;
import com.guide.touristmodel.entity.Destination;
import com.guide.touristservice.dto.BookingAutoCompleteResponse;
import com.guide.touristservice.dto.BookingSearchResponse;
import com.guide.touristservice.integration.BookingApiClient;
import com.guide.touristrepository.repository.AttractionRepository;
import com.guide.touristrepository.repository.DestinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingAttractionService {

    private final BookingApiClient apiClient;
    private final DestinationRepository destinationRepo;
    private final AttractionRepository attractionRepo;


    @Cacheable(cacheNames = "destinationsByPrefix")
    public List<Destination> getSuggestionsFromDb(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return destinationRepo.findAll();
        }
        return destinationRepo.findByCityNameContainingIgnoreCase(prefix);
    }

    public List<Destination> importAllDestinationsFromAutocomplete(String typed) {
        BookingAutoCompleteResponse autoResp = apiClient.autoComplete(typed);
        if (autoResp == null || !autoResp.isStatus()
                || autoResp.getData() == null
                || autoResp.getData().getDestinations() == null) {
            return List.of();
        }
        var items = autoResp.getData().getDestinations();
        if (items.isEmpty()) {
            return List.of();
        }
        List<Destination> savedDestinations = new ArrayList<>();
        for (var item : items) {
            Destination dest = saveOrUpdateDestination(item);
            savedDestinations.add(dest);
        }
        return savedDestinations;
    }

    @Transactional
    public List<Attraction> importAttractionsForDestination(Long destinationId) {
        Destination d = destinationRepo.findById(destinationId).orElse(null);
        if (d == null) {
            return List.of();
        }

        BookingSearchResponse searchResp = apiClient.searchAttractions(d.getBookingIdBase64());
        saveSearchResults(d, searchResp);


        d = destinationRepo.findById(destinationId).orElse(null);
        if (d == null) {
            return List.of();
        }
        return d.getAttractions();
    }


    private Destination saveOrUpdateDestination(BookingAutoCompleteResponse.DestinationItem item) {
        Destination existing = destinationRepo.findByCityName(item.getCityName());
        if (existing == null) {
            existing = new Destination();
            existing.setCityName(item.getCityName());
        }
        existing.setCountry(item.getCountry());
        existing.setBookingIdBase64(item.getId());
        existing.setUfi(item.getUfi());
        existing.setProductCount(item.getProductCount());

        return destinationRepo.save(existing);
    }

    private List<Attraction> saveSearchResults(Destination d, BookingSearchResponse searchResp) {
        if (!searchResp.isStatus() || searchResp.getData() == null ||
                searchResp.getData().getProducts() == null) {
            return List.of();
        }
        var products = searchResp.getData().getProducts();
        List<Attraction> saved = new ArrayList<>();
        for (var p : products) {
            var existing = attractionRepo.findByBookingProductIdAndDestinationId(p.getId(), d.getId());
            Attraction a;

            if (existing != null) {
                a = existing;


            } else {
                a = new Attraction();
                a.setDestination(d);
                a.setBookingProductId(p.getId());
                a.setName(p.getName());
                a.setSlug(p.getSlug());
                a.setDescription(p.getShortDescription());
            }

            var repPrice = p.getRepresentativePrice();
            if (repPrice != null) {
                a.setPrice(repPrice.getPublicAmount());
                a.setCurrency(repPrice.getCurrency());
            } else {
                a.setPrice(0.0);
                a.setCurrency("USD");
            }

            if (p.getReviewsStats() != null && p.getReviewsStats().getCombinedNumericStats() != null) {
                a.setReviewsAverage(p.getReviewsStats().getCombinedNumericStats().getAverage());
                a.setReviewsTotal(p.getReviewsStats().getCombinedNumericStats().getTotal());
            }

            if (p.getPrimaryPhoto() != null) {
                a.setPhotoUrl(p.getPrimaryPhoto().getSmall());
            }

            if (p.getUfiDetails() != null) {
                a.setLat(p.getUfiDetails().getLatitude());
                a.setLng(p.getUfiDetails().getLongitude());
            }

            attractionRepo.save(a);
            saved.add(a);
        }
        return saved;
    }
    public Destination getDestinationById(Long id) {
        return destinationRepo.findById(id).orElse(null);
    }

    public Destination getDestinationByNameIgnoreCase(String cityName) {
        return destinationRepo.findByCityNameIgnoreCase(cityName);
    }
}