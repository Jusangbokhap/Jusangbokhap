package jsbh.Jusangbokhap.api.accommodation.dto;

public interface AccommodationRequest {
    record Register(
            String address,
            String description,
            Integer price,
            String accommodationType,
            Integer personnel,
            Integer userId
    ) implements AccommodationRequest{
    }
}
