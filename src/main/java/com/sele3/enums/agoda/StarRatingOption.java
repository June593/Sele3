package com.sele3.enums.agoda;

import com.sele3.utils.YamlUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StarRatingOption implements FilterValue {

    RATING_THREE_STAR("3_stars"),
    RATING_FOUR_STAR("4_stars");

    private final String value;

    @Override
    public String toString() {
        return (String) YamlUtils.getProperty("star_rating_options." + this.value);
    }
}
