package jsbh.Jusangbokhap.api.accommodation.dto;

public interface AccommodationResponse {
    record Register(
            Long accommodationId,
            Long userId
    ) implements AccommodationResponse {
    }
}
