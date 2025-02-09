package com.guide.touristweb.controller;

import com.guide.touristmodel.entity.Attraction;
import com.guide.touristmodel.entity.Destination;
import com.guide.touristservice.services.BookingAttractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final BookingAttractionService bookingService;

    @GetMapping("/")
    public String home(Model model) {
        List<Destination> all = bookingService.getSuggestionsFromDb("");
        model.addAttribute("suggestions", all);
        return "home";
    }

    @GetMapping("/search")
    public String searchByCity(@RequestParam String city, Model model) {
        Destination existing = bookingService.getDestinationByNameIgnoreCase(city);
        if (existing != null) {
            List<Attraction> attractions = bookingService.importAttractionsForDestination(existing.getId());
            model.addAttribute("cityName", existing.getCityName());
            model.addAttribute("attractions", attractions);
            return "results";
        }

        List<Destination> newlySaved = bookingService.importAllDestinationsFromAutocomplete(city);
        if (newlySaved == null || newlySaved.isEmpty()) {
            model.addAttribute("message", "No destinations found for: " + city);
            return "error";
        } else if (newlySaved.size() == 1) {
            Destination single = newlySaved.get(0);
            List<Attraction> attractions = bookingService.importAttractionsForDestination(single.getId());
            model.addAttribute("cityName", single.getCityName());
            model.addAttribute("attractions", attractions);
            return "results";
        } else {
            model.addAttribute("destinations", newlySaved);
            return "chooseDestination";
        }
    }

    @GetMapping("/pickDestination")
    public String pickDestination(@RequestParam("id") Long id, Model model) {
        Destination d = bookingService.getDestinationById(id);
        if (d == null) {
            model.addAttribute("message", "Destination not found for ID: " + id);
            return "error";
        }
        List<Attraction> attractions = bookingService.importAttractionsForDestination(d.getId());
        model.addAttribute("cityName", d.getCityName());
        model.addAttribute("attractions", attractions);
        return "results";
    }

}