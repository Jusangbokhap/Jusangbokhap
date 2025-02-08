package jsbh.Jusangbokhap.api.availableDate.mapper;

import jsbh.Jusangbokhap.api.availableDate.dto.AvailableDateResponse;
import jsbh.Jusangbokhap.domain.availableDate.AvailableDate;

public class AvailableDateMapper {
    public static AvailableDateResponse toResponse(AvailableDate availableDate) {
        return new AvailableDateResponse(
                availableDate.getStartDate(),
                availableDate.getEndDate(),
                availableDate.getStatus()
        );
    }
}
