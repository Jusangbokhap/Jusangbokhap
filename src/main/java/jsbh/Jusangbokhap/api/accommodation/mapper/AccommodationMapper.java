package jsbh.Jusangbokhap.api.accommodation.mapper;


import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.stream.Collectors;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest.Create;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationResponse.Read;
import jsbh.Jusangbokhap.api.availableDate.mapper.AvailableDateMapper;
import jsbh.Jusangbokhap.domain.accommodation.*;
import org.locationtech.jts.geom.Point;

public class AccommodationMapper {

    public static Accommodation toEntity(Create request) {

        Point coordinate = AccommodationCoordinate
                .createCoordinate(request.longitude(), request.latitude());

        return Accommodation.builder()

                .title(request.title())

                .businessName(request.businessName())

                .address(AccommodationAddress.builder()
                        .sido(request.sido())
                        .sigungu(request.sigungu())
                        .eupmyeondong(request.eupmyeondong())
                        .detail(request.detail())
                        .coordinate(new AccommodationCoordinate(coordinate))
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
                accommodation.getAddress().getCoordinate().getCoordinate().getX(),
                accommodation.getAddress().getCoordinate().getCoordinate().getY(),
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
