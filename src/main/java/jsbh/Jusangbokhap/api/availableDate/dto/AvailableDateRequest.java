package jsbh.Jusangbokhap.api.availableDate.dto;

import java.time.LocalDate;

public record AvailableDateRequest(LocalDate startDate, LocalDate endDate) {
}
