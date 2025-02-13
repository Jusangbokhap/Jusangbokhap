package jsbh.Jusangbokhap.api.accommodation.mapper;

import java.util.ArrayList;
import java.util.stream.Collectors;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest.Create;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationResponse.Read;
import jsbh.Jusangbokhap.api.availableDate.mapper.AvailableDateMapper;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import jsbh.Jusangbokhap.domain.accommodation.AccommodationPrice;
import jsbh.Jusangbokhap.domain.accommodation.AccommodationType;
import jsbh.Jusangbokhap.domain.accommodation.AccommodationCapacity;

public class AccommodationMapper {
    public static Accommodation toEntity(Create dto) {
        return Accommodation.builder()
                .address(dto.address())
                .accommodationPrice(new AccommodationPrice(dto.price()))
                .accommodationType(AccommodationType.from(dto.accommodationType()))
                .description(dto.description())
                .maxGuests(new AccommodationCapacity(dto.personnel()))
                .availableDates(new ArrayList<>())
                .imageUrl(null) // TODO: 이미지 저장 기능 추가 후 수정
                .host(null) // TODO: User Service 개발 후 수정
                .build();
    }

    public static Read toResponse(Accommodation accommodation) {
        return new Read(
                accommodation.getAddress(),
                accommodation.getDescription(),
                accommodation.getAccommodationPrice().getPrice(),
                accommodation.getAccommodationType().name(),
                accommodation.getMaxGuests().getMaxGuest(),
                1L,
                accommodation.getAvailableDates().getDates().stream()
                        .map(AvailableDateMapper::toResponse)
                        .collect(Collectors.toList())
        );
    }
}
