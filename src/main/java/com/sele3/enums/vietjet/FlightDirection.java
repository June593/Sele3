package com.sele3.enums.vietjet;

import com.sele3.utils.YamlUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FlightDirection {

    DEPARTURE("departure_flight"),
    RETURN("return_flight");

    private final String code;

    @Override
    public String toString() {
        return (String) YamlUtils.getProperty("label_Passenger_Page." + this.code);
    }
}
