package com.sele3.enums.agoda;

import com.sele3.utils.YamlUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PropertyFacilitiesOptions implements FilterValue {

    NON_SMOKING("non_smoking"),
    SWIMMING_POOL("swimming_pool");

    private final String value;

    @Override
    public String toString() {
        return (String) YamlUtils.getProperty("property_facilities_options." + this.value);
    }
}
