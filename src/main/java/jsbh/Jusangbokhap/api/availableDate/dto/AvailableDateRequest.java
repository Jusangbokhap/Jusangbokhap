package jsbh.Jusangbokhap.api.availableDate.dto;

import java.time.LocalDate;
import jsbh.Jusangbokhap.domain.availableDate.AvailableDateStatus;

public record AvailableDateRequest(LocalDate startDate, LocalDate endDate, AvailableDateStatus status) {
}
