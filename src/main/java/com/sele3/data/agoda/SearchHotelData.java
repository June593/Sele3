package com.sele3.data.agoda;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
@Data
@Builder
public class SearchHotelData {
    private Boolean isDayUseStays;
    private String place;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Occupancy occupancy;

    @Data
    @Builder
    public static class Occupancy {
        private Integer room;
        private Integer adults;
        private Integer children;
    }
}
