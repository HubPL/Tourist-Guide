package com.guide.touristservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingSearchResponse {

    private DataInner data;
    private boolean status;
    private String message;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataInner {
        private List<ProductItem> products;

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductItem {

        private String id;
        private String name;
        private String slug;

        private String shortDescription;
        private RepresentativePrice representativePrice;
        private ReviewsStats reviewsStats;
        private PrimaryPhoto primaryPhoto;
        private UfiDetails ufiDetails;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class RepresentativePrice {
            private double publicAmount;
            private String currency;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ReviewsStats {
            private CombinedNumericStats combinedNumericStats;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class CombinedNumericStats {
                private double average;
                private int total;
            }
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class PrimaryPhoto {
            private String small;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class UfiDetails {
            private long ufi;
            private double latitude;
            private double longitude;
            private String bCityName;
        }
    }
}
