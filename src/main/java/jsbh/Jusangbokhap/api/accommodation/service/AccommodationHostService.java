package jsbh.Jusangbokhap.api.accommodation.service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest.Create;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationResponse;
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

    public AccommodationResponse update(Long accommodationId, AccommodationRequest.Update request) {
        Accommodation accommodation = getAccommodationByAccommodationId(accommodationId);

        accommodation.updateDetails(
                request.title(),
                request.sido(),
                request.sigungu(),
                request.eupmyeondong(),
                request.detail(),
                request.longitude(),
                request.latitude(),
                request.description(),
                request.price(),
                request.accommodationType(),
                request.guests());

        return new AccommodationResponse.Update(accommodationId);
    }


    public AccommodationResponse delete(Long accommodationId) {
        accommodationRepository.delete(getAccommodationByAccommodationId(accommodationId));
        return new AccommodationResponse.Delete(accommodationId);
    }

    private Accommodation getAccommodationByAccommodationId(Long accommodationId) {
        return accommodationRepository
                .findByAccommodationId(accommodationId)
                .orElseThrow(() -> new AccommodationCustomException(AccommodationErrorCode.NOT_FOUND_ACCOMMODATION));
    }

    private List<Accommodation> getAccommodationByHostId(Long hostId) {
        return accommodationRepository.findByHostId(hostId);
    }

        //TODO 분리 예정
//    public AccommodationResponse updateAccommodationAvailableDate(Long accommodationId,
//                                                                  AccommodationRequest.UpdateAvailableDate request) {
//        Accommodation accommodation = findById(accommodationId);
//
//        AvailableDate availableDate = availableDateService.updateAvailableDate(request.availableDateId(),
//                request.startDate(), request.endDate(), AvailableDateStatus.AVAILABLE);
//
//        accommodation.updateAvailableDate(availableDate);
//
//        return new AccommodationResponse.Update(accommodationId);
//    }

}
