package com.sele3.enums.agoda;

import com.sele3.utils.YamlUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

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

    private static final Map<String, String> DISPLAY_LABELS = new HashMap<>();

    static {
        for (ReviewType type : values()) {
            String label = (String) YamlUtils.getProperty("review_type." + type.value);
            DISPLAY_LABELS.put(type.value, label);
        }
    }

    @Override
    public String toString() {
        return DISPLAY_LABELS.getOrDefault(this.value, this.value);
    }

    public static ReviewType fromText(String text) {
        for (ReviewType type : values()) {
            String label = DISPLAY_LABELS.get(type.value);
            if (label != null && label.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown ReviewType display value: " + text);
    }
}
