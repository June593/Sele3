package com.sele3.data.agoda;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HotelData {

    String address;
    String name;
    float star;
    int price;
}