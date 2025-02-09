package com.guide.touristweb.controller;

import com.guide.touristmodel.entity.Attraction;
import com.guide.touristmodel.entity.Destination;
import com.guide.touristrepository.repository.AttractionRepository;
import com.guide.touristrepository.repository.DestinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DestinationRepository destinationRepo;
    private final AttractionRepository attractionRepo;

    @GetMapping("/destinations")
    public String destinationsList(Model model) {
        model.addAttribute("destinations", destinationRepo.findAll());
        return "adminDestinations";
    }

    @GetMapping("/destinations/delete")
    @Transactional
    public String deleteDestination(@RequestParam("destId") Long destId, Model model) {
        var dest = destinationRepo.findById(destId).orElse(null);
        if (dest == null) {
            model.addAttribute("error", "Destination not found.");
            return "redirect:/admin/destinations";
        }
        destinationRepo.delete(dest);
        return "redirect:/admin/destinations";
    }


    @GetMapping("/attractions/form")
    public String showAttractionForm(@RequestParam(value = "attrId", required = false) Long attrId,
                                     @RequestParam(value = "destId", required = false) Long destId,
                                     Model model) {
        Attraction attraction;
        Destination destination;
        boolean editMode = false;

        if (attrId != null) {
            attraction = attractionRepo.findById(attrId).orElse(null);
            if (attraction == null) {
                model.addAttribute("error", "Attraction not found.");
                return "redirect:/admin/destinations";
            }
            destination = attraction.getDestination();
            editMode = true;
        } else {
            if (destId == null) {
                model.addAttribute("error", "Destination ID is required for adding a new attraction.");
                return "redirect:/admin/destinations";
            }
            destination = destinationRepo.findById(destId).orElse(null);
            if (destination == null) {
                model.addAttribute("error", "Destination not found.");
                return "redirect:/admin/destinations";
            }
            attraction = new Attraction();
            attraction.setDestination(destination);
        }

        model.addAttribute("attraction", attraction);
        model.addAttribute("destination", destination);
        model.addAttribute("editMode", editMode);

        return "adminAttractionForm";
    }


    @PostMapping("/attractions/save")
    @Transactional
    public String saveAttraction(@ModelAttribute("attraction") Attraction formAttraction,
                                 @RequestParam(value = "editMode", defaultValue = "false") boolean editMode,
                                 Model model) {
        if (formAttraction.getName() == null || formAttraction.getName().trim().isEmpty()) {
            model.addAttribute("error", "Name is required.");
            model.addAttribute("editMode", editMode);
            model.addAttribute("attraction", formAttraction);
            model.addAttribute("destination", formAttraction.getDestination());
            return "adminAttractionForm";
        }

        if (editMode) {
            Attraction existing = attractionRepo.findById(formAttraction.getId()).orElse(null);
            if (existing == null) {
                model.addAttribute("error", "Attraction not found for editing.");
                return "redirect:/admin/destinations";
            }
            existing.setName(formAttraction.getName());
            existing.setSlug(formAttraction.getSlug());
            existing.setDescription(formAttraction.getDescription());
            existing.setPhotoUrl(formAttraction.getPhotoUrl());
            existing.setPrice(formAttraction.getPrice());
            existing.setCurrency(formAttraction.getCurrency());
            existing.setLat(formAttraction.getLat());
            existing.setLng(formAttraction.getLng());

            attractionRepo.save(existing);

        } else {
            var destId = formAttraction.getDestination().getId();
            Destination dest = destinationRepo.findById(destId).orElse(null);
            if (dest == null) {
                model.addAttribute("error", "Destination not found.");
                return "redirect:/admin/destinations";
            }
            formAttraction.setDestination(dest);
            attractionRepo.save(formAttraction);
        }

        return "redirect:/admin/destinations";
    }


    @GetMapping("/attractions/delete")
    @Transactional
    public String deleteAttraction(@RequestParam("attrId") Long attrId, Model model) {
        var attr = attractionRepo.findById(attrId).orElse(null);
        if (attr == null) {
            model.addAttribute("error", "Attraction not found.");
            return "redirect:/admin/destinations";
        }
        attractionRepo.delete(attr);
        return "redirect:/admin/destinations";
    }
}