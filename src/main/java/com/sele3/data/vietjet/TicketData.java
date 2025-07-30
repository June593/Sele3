package com.sele3.data.vietjet;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TicketData {
    String from;
    String to;
    LocalDate departureDate;
    LocalDate returnDate;
}
