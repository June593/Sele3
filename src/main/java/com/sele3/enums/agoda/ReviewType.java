package com.sele3.enums.agoda;

import com.sele3.utils.YamlUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewType {
    LOCATION("location"),
    CLEANLINESS("cleanliness"),
    SERVICE("service"),
    FACILITIES("facilities"),
    ROOM_COMFORT_AND_QUALITY("room_comfort_and_quantity"),
    VALUE_FOR_MONEY("value_for_money");

    private final String value;

    @Override
    public String toString() {
        return (String) YamlUtils.getProperty("review_type." + value);
    }

    public static ReviewType fromText(String text) {
        for (ReviewType type : values()) {
            String label = (String) YamlUtils.getProperty("review_type." + type.value);
            if (label != null && label.equalsIgnoreCase(text)) {
                return type;
            }
            if(text.equals("Room comfort and qua...")) {
                return ROOM_COMFORT_AND_QUALITY;
            }
        }
        throw new IllegalArgumentException("Unknown ReviewType display value: " + text);
    }
}
