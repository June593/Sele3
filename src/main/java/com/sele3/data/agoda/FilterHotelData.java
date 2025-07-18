package com.sele3.data.agoda;


import com.sele3.enums.agoda.FilterType;
import com.sele3.enums.agoda.FilterValue;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FilterHotelData {

    int minPrice;
    int maxPrice;
    List<Filter> filters;

    @Data
    @Builder
    public static class Filter {
        FilterType filterType;
        FilterValue filterValue;
    }
}
