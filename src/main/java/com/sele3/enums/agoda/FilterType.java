package com.sele3.enums.agoda;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.sele3.utils.YamlUtils;

@Getter
@AllArgsConstructor
public enum FilterType {

    POPULAR_FILTER("popular_filter"),
    PROPERTY_FACILITIES("property_facilities"),
    STAR_RATING("star_rating");

    private final String value;

    @Override
    public String toString() {
        return (String) YamlUtils.getProperty("filter_types." + this.value);
    }
}
