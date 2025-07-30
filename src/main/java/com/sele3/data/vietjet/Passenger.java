package com.sele3.data.vietjet;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Passenger {
    private Integer adultNumber;
    private Integer childrenNumber;
    private Integer infantNumber;
}