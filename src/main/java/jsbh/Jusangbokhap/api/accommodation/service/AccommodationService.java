package jsbh.Jusangbokhap.api.accommodation.service;

import jakarta.transaction.Transactional;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationResponse;
import jsbh.Jusangbokhap.api.accommodation.mapper.AccommodationMapper;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import jsbh.Jusangbokhap.domain.accommodation.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;

    public AccommodationResponse createAccommodation(AccommodationRequest.Register request) {
        Accommodation accommodation = AccommodationMapper.toEntity(request);
        accommodationRepository.save(accommodation);
        //TODO User Service 개발 완료 시 사용자를 찾아오는 방식으로 수정
        return new AccommodationResponse.Register(accommodation.getAccommodationId(), 1L);
    }
}
