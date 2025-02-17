package jsbh.Jusangbokhap.api.accommodation.service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest.Create;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationResponse;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationResponse.Address;
import jsbh.Jusangbokhap.api.accommodation.exception.AccommodationCustomException;
import jsbh.Jusangbokhap.api.accommodation.exception.AccommodationErrorCode;
import jsbh.Jusangbokhap.api.accommodation.mapper.AccommodationMapper;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import jsbh.Jusangbokhap.domain.accommodation.repository.AccommodationRepository;
import jsbh.Jusangbokhap.domain.availableDate.AvailableDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AccommodationHostService {

    private final AccommodationRepository accommodationRepository;
    private final AccommodationService accommodationService;

    public AccommodationResponse create(Create request) {
        Accommodation accommodation = AccommodationMapper.toEntity(request);

        request.availableDates().forEach(date -> {
            AvailableDate availableDate = new AvailableDate(date.startDate(), date.endDate(), date.status());
            accommodation.addAvailableDate(availableDate);
        });

        accommodationRepository.save(accommodation);

        return new AccommodationResponse.Create(accommodation.getAccommodationId());
    }

    public AccommodationResponse findByAccommodationId(Long accommodationId) {
        return AccommodationMapper.toResponse(getAccommodationByAccommodationId(accommodationId));
    }

    public List<AccommodationResponse> findByHostId(Long hostId) {
        List<Accommodation> accommodations = getAccommodationByHostId(hostId);

        List<AccommodationResponse> responses = new ArrayList<>();
        for (Accommodation accommodation : accommodations) {
            responses.add(AccommodationMapper.toResponse(accommodation));
        }

        return responses;
    }

    public AccommodationResponse delete(Long accommodationId) {
        accommodationRepository.delete(getAccommodationByAccommodationId(accommodationId));
        return new AccommodationResponse.Delete(accommodationId);
    }

    public Accommodation getAccommodationByAccommodationId(Long accommodationId) {
        return accommodationRepository
                .findByAccommodationId(accommodationId)
                .orElseThrow(() -> new AccommodationCustomException(AccommodationErrorCode.NOT_FOUND_ACCOMMODATION));
    }

    private List<Accommodation> getAccommodationByHostId(Long hostId) {
        return accommodationRepository.findByHostId(hostId);
    }

    public AccommodationResponse.Address getAccommodationAddressById(Long accommodationId) {
        Accommodation accommodation = getAccommodationByAccommodationId(accommodationId);
        return new Address(accommodation.getAddress().getLatitude(), accommodation.getAddress().getLatitude());
    }

    public AccommodationResponse update(Long accommodationId, AccommodationRequest.Update request) {
        return accommodationService.update(accommodationId, request);
    }
}
