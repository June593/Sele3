package com.sele3.enums.agoda;

import com.sele3.utils.YamlUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PopularFilterOption implements FilterValue {

    BREAKFAST_INCLUDED("Breakfast included"),
    FREE_CANCELLATION("Free cancellation"),
    PAY_AT_HOTEL("Pay at the hotel"),
    FREE_WIFI("Free Wi-Fi"),
    SWIMMING_POOL("Swimming pool");

    private final String value;

    @Override
    public String toString() {
        return (String) YamlUtils.getProperty("filter." + this.value);
    }
}
