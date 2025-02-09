package com.guide.touristweb.controller;

import com.guide.touristmodel.entity.Attraction;
import com.guide.touristrepository.repository.AttractionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AttractionController {

    private final AttractionRepository attractionRepo;

    @GetMapping("/attraction/details")
    @Transactional
    public String showAttractionDetails(@RequestParam("attrId") Long attrId,
                                        Model model) {
        Attraction attraction = attractionRepo.findById(attrId).orElse(null);
        if (attraction == null) {
            model.addAttribute("message", "Attraction not found.");
            return "error";
        }

        model.addAttribute("attraction", attraction);
        return "attractionDetails";
    }
}