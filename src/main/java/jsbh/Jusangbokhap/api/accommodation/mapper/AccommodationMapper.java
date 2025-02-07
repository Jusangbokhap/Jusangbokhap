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
                //TODO 사진 저장 기능 추가 후 수정
                .imageUrl(null)
                //TODO User Service 개발 완료 시 사용자를 찾아오는 방식으로 수정
                .host(null)
                .build();
    }
}
