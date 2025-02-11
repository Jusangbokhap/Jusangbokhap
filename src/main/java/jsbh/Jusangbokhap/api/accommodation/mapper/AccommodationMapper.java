package jsbh.Jusangbokhap.api.accommodation.mapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.stream.Collectors;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest.Create;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationResponse.Read;
import jsbh.Jusangbokhap.api.availableDate.mapper.AvailableDateMapper;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import jsbh.Jusangbokhap.domain.accommodation.AccommodationAddress;
import jsbh.Jusangbokhap.domain.accommodation.AccommodationPrice;
import jsbh.Jusangbokhap.domain.accommodation.AccommodationType;
import jsbh.Jusangbokhap.domain.accommodation.AccommodationCapacity;

public class AccommodationMapper {

    public static Accommodation toEntity(Create request) {

        return Accommodation.builder()

                .title(request.title())

                .businessName(request.businessName())

                .address(AccommodationAddress.builder()
                        .sido(request.sido())
                        .sigungu(request.sigungu())
                        .eupmyeondong(request.eupmyeondong())
                        .detail(request.detail())
                        .longitude(request.longitude())
                        .latitude(request.latitude())
                        .build())

                .accommodationPrice(AccommodationPrice.from(request.price()))

                .accommodationType(AccommodationType.from(request.accommodationType()))

                .maxGuests(AccommodationCapacity.from(request.guests()))

                .description(request.description())

                .availableDates(new ArrayList<>())

                .imageUrl(null) // TODO: 이미지 저장 기능 추가 후 수정

                .host(null) // TODO: User Service 개발 후 수정

                .build();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static Read toResponse(Accommodation accommodation) {
        return new Read(
                accommodation.getBusinessName(),
                accommodation.getTitle(),
                accommodation.getAddress().getFullAddress(),
                accommodation.getDescription(),
                accommodation.getAccommodationPrice().getPrice(),
                accommodation.getAccommodationType().name(),
                accommodation.getMaxGuests().getMaxGuest(),
                1L,
                accommodation.getAvailableDates()
                        .getDates()
                        .stream()
                        .map(AvailableDateMapper::toResponse)
                        .collect(Collectors.toList())
        );
    }
}
