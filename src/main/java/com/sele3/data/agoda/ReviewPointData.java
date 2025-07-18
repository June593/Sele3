package com.sele3.data.agoda;

import com.sele3.enums.agoda.ReviewType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewPointData {
    ReviewType reviewName;
    Float point;
}
