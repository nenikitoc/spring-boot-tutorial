package com.example.reservation;

import java.time.LocalDate;

public record Reservation (
        Long Id,
        Long userId,
        Long roomId,
        LocalDate startDate,
        LocalDate endDate,
        ReservationStatus status
){
}
