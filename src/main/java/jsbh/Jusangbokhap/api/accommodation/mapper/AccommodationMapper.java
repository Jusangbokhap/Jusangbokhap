package jsbh.Jusangbokhap.api.accommodation.mapper;

import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import jsbh.Jusangbokhap.domain.accommodation.AccommodationType;
import jsbh.Jusangbokhap.domain.accommodation.Personnel;

public class AccommodationMapper {
    public static Accommodation toEntity(AccommodationRequest.Register dto) {
        return Accommodation.builder()
                .address(dto.address())
                .price(dto.price())
                .accommodationType(AccommodationType.valueOf(dto.accommodationType()))
                .description(dto.description())
                .maxGuests(new Personnel(dto.personnel()))
                .imageUrl(null) // TODO: 이미지 저장 기능 추가 후 수정
                .host(null) // TODO: User Service 개발 후 수정
                .build();
    }
}
