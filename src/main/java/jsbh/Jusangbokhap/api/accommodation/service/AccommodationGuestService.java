package jsbh.Jusangbokhap.api.accommodation.service;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationResponse;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationResponse.Search;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import jsbh.Jusangbokhap.domain.accommodation.AccommodationType;
import jsbh.Jusangbokhap.domain.accommodation.QAccommodation;
import jsbh.Jusangbokhap.domain.accommodation.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccommodationGuestService {

    private final AccommodationRepository accommodationRepository;

    @Transactional
    public List<AccommodationResponse> find(AccommodationRequest.Search filter) {
        Predicate predicate = buildPredicate(filter);

        List<Accommodation> accommodations = accommodationRepository.findAvailableAccommodations(predicate);
        List<AccommodationResponse> responses = new ArrayList<>();
        for (Accommodation accommodation : accommodations) {
            Search search = new Search(
                    accommodation.getAccommodationId(),
                    accommodation.getBusinessName(),
                    accommodation.getTitle(),
                    accommodation.getAddress().getFullAddress(),
                    accommodation.getAccommodationPrice().getPrice(),
                    accommodation.getAvailableDates()
                            .calculateTotalPrice(accommodation.getAccommodationPrice().getPrice())
            );
            responses.add(search);
        }
        return responses;
    }

    private Predicate buildPredicate(AccommodationRequest.Search filter) {
        BooleanBuilder builder = new BooleanBuilder();

        builder = filterByBusinessName(builder, filter.businessName());
        builder = filterByGuests(builder, filter.guests());
        builder = filterByDate(builder, filter.checkin(), filter.checkout());
        builder = filterByPrice(builder, filter.minPrice(), filter.maxPrice());
        builder = filterByType(builder, filter.type());
        builder = filterByAddress(builder, filter.sido(), filter.sigungu(), filter.eupmyeondong(), filter.detail());

        log.info("Generated SQL: {}", builder.toString());
        return builder;
    }

    private BooleanBuilder filterByBusinessName(BooleanBuilder builder, String businessName) {
        if (businessName != null && !businessName.trim().isEmpty()) {
            builder.and(QAccommodation.accommodation.businessName.contains(businessName));
        }
        return builder;
    }

    private BooleanBuilder filterByGuests(BooleanBuilder builder, Integer guests) {
        if (guests != null) {
            builder.and(QAccommodation.accommodation.maxGuests.maxGuest.goe(guests));
        }
        return builder;
    }

    private BooleanBuilder filterByDate(BooleanBuilder builder, LocalDate checkin, LocalDate checkout) {
        if (checkin != null && checkout != null) {
            builder.and(QAccommodation.accommodation.availableDates.any().checkin.goe(checkin));
            builder.and(QAccommodation.accommodation.availableDates.any().checkout.loe(checkout));
        }
        return builder;
    }

    private BooleanBuilder filterByPrice(BooleanBuilder builder, Integer minPrice, Integer maxPrice) {
        if (minPrice != null && maxPrice != null) {
            builder.and(QAccommodation.accommodation.accommodationPrice.price.between(minPrice, maxPrice));
        }
        return builder;
    }

    private BooleanBuilder filterByType(BooleanBuilder builder, AccommodationType type) {
        if (type != null) {
            builder.and(QAccommodation.accommodation.accommodationType.eq(type));
        }
        return builder;
    }

    private BooleanBuilder filterByAddress(BooleanBuilder builder, String sido, String sigungu, String eupmyeondong, String detail) {

        if (sido != null && !sido.trim().isEmpty()) {
            builder.and(QAccommodation.accommodation.address.sido.eq(sido));
        }

        if (sigungu != null && !sigungu.trim().isEmpty()) {
            builder.and(QAccommodation.accommodation.address.sigungu.eq(sigungu));
        }

        if (eupmyeondong != null && !eupmyeondong.trim().isEmpty()) {
            builder.and(QAccommodation.accommodation.address.eupmyeondong.eq(eupmyeondong));
        }

        if (detail != null && !detail.trim().isEmpty()) {
            builder.and(QAccommodation.accommodation.address.detail.eq(detail));
        }

        return builder;
    }
}
