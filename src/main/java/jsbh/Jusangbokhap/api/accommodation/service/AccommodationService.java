package jsbh.Jusangbokhap.api.accommodation.service;

import jakarta.transaction.Transactional;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationResponse;
import jsbh.Jusangbokhap.api.accommodation.mapper.AccommodationMapper;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import jsbh.Jusangbokhap.domain.accommodation.repository.AccommodationRepository;
import jsbh.Jusangbokhap.domain.availableDate.AvailableDate;
import jsbh.Jusangbokhap.domain.availableDate.AvailableDateStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;

    public AccommodationResponse createAccommodation(AccommodationRequest.Register request) {
        Accommodation accommodation = AccommodationMapper.toEntity(request);

        request.availableDates().forEach(date -> accommodation.addAvailableDate(
                new AvailableDate(date.startDate(), date.endDate(), AvailableDateStatus.AVAILABLE)));

        accommodationRepository.save(accommodation);
        return new AccommodationResponse.Register(accommodation.getAccommodationId(), 1L);
    }
}
