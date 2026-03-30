package com.example.reservation;

import java.time.LocalDate;

// record - компактный, неизменяемый контейнер данных, сам сгенерирует get, equals, hashCode и toString
public record Reservation (
        Long id,
        Long userId,
        Long roomId,
        LocalDate startDate,
        LocalDate endDate,
        ReservationStatus status
){
}
