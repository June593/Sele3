package com.sele3.enums.vietjet;

import com.sele3.utils.YamlUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LabelType {

    FROM("from"),
    TO("to"),
    PASSENGER("passenger"),
    ADULT("adult"),
    CHILDREN("children"),
    INFANT("infant");

    private final String code;

    @Override
    public String toString() {
        return (String) YamlUtils.getProperty("labelType." + this.code);
    }
}
