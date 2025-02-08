package jsbh.Jusangbokhap.api.availableDate.dto;

import java.time.LocalDate;
import jsbh.Jusangbokhap.domain.availableDate.AvailableDateStatus;

public record AvailableDateResponse(LocalDate startDate,LocalDate endDate,AvailableDateStatus status) {
}
