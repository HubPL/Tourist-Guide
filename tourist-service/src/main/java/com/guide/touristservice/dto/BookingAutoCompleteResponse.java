package com.guide.touristservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingAutoCompleteResponse {

    private DataInner data;
    private boolean status;
    private String message;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataInner {
        private List<DestinationItem> destinations;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DestinationItem {
        private String country;
        private String cc1;
        private String cityName;
        private long ufi;
        private String id;
        private int ProductCount;

    }
}
