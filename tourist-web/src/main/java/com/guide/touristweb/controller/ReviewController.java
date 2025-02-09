package com.guide.touristweb.controller;

import com.guide.touristmodel.entity.Attraction;
import com.guide.touristmodel.entity.Review;
import com.guide.touristmodel.entity.User;
import com.guide.touristrepository.repository.AttractionRepository;
import com.guide.touristrepository.repository.ReviewRepository;
import com.guide.touristrepository.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final AttractionRepository attractionRepo;
    private final ReviewRepository reviewRepo;
    private final UserRepository userRepo;

    @GetMapping("/add")
    public String addReviewForm(@RequestParam("attrId") Long attrId, Model model) {
        var attraction = attractionRepo.findById(attrId).orElse(null);
        if (attraction == null) {
            model.addAttribute("message", "Attraction not found.");
            return "error";
        }

        model.addAttribute("attraction", attraction);
        model.addAttribute("review", new Review());
        return "reviewForm";
    }

    @PostMapping("/add")
    @Transactional
    public String addReview(@RequestParam("attrId") Long attrId,
                            @RequestParam("rating") int rating,
                            @RequestParam("comment") String comment,
                            Authentication authentication,
                            Model model) {

        Attraction attraction = attractionRepo.findById(attrId).orElse(null);
        if (attraction == null) {
            model.addAttribute("message", "Attraction not found.");
            return "error";
        }

        User user = userRepo.findByUsername(authentication.getName());
        if (user == null) {
            model.addAttribute("message", "User not found.");
            return "error";
        }

        if (rating < 1 || rating > 5) {
            model.addAttribute("error", "Rating must be between 1 and 5.");
            model.addAttribute("attraction", attraction);
            return "reviewForm";
        }

        if (comment == null || comment.trim().isEmpty()) {
            model.addAttribute("error", "Comment cannot be empty.");
            model.addAttribute("attraction", attraction);
            return "reviewForm";
        }

        Review review = new Review();
        review.setRating(rating);
        review.setComment(comment);
        review.setUser(user);
        attraction.addReview(review);

        attractionRepo.save(attraction);
        updateAttractionRating(attraction);

        return "redirect:/attraction/details?attrId=" + attrId;
    }

    @GetMapping("/delete")
    @Transactional
    public String deleteReview(@RequestParam("reviewId") Long reviewId,
                               @RequestParam("attrId") Long attrId,
                               Authentication authentication,
                               Model model) {

        Attraction attraction = attractionRepo.findById(attrId).orElse(null);
        if (attraction == null) {
            model.addAttribute("message", "Attraction not found.");
            return "error";
        }

        Review review = reviewRepo.findById(reviewId).orElse(null);
        if (review == null) {
            model.addAttribute("message", "Review not found.");
            return "error";
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !review.getUser().getUsername().equals(authentication.getName())) {
            model.addAttribute("message", "Not authorized to delete this review.");
            return "error";
        }

        attraction.removeReview(review);
        attractionRepo.save(attraction);
        reviewRepo.delete(review);

        updateAttractionRating(attraction);

        return "redirect:/attraction/details?attrId=" + attrId;
    }

    private void updateAttractionRating(Attraction attraction) {
        List<Review> reviews = attraction.getReviews();
        if (reviews.isEmpty()) {
            attraction.setReviewsAverage(0.0);
            attraction.setReviewsTotal(0);
        } else {
            double average = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            attraction.setReviewsAverage(average);
            attraction.setReviewsTotal(reviews.size());
        }
        attractionRepo.save(attraction);
    }
}