package jsbh.Jusangbokhap.api.availableDate.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import jsbh.Jusangbokhap.api.availableDate.exception.AvailableDateCustomException;
import jsbh.Jusangbokhap.api.availableDate.exception.AvailableDateErrorCode;
import jsbh.Jusangbokhap.domain.availableDate.AvailableDate;
import jsbh.Jusangbokhap.domain.availableDate.AvailableDateStatus;
import jsbh.Jusangbokhap.domain.availableDate.repository.AvailableDateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AvailableDateService {

    private final AvailableDateRepository availableDateRepository;

    public AvailableDate updateAvailableDate(Long availableDateById, LocalDate startDate, LocalDate endDate,
                                             AvailableDateStatus status) {
        AvailableDate availableDate = getAvailableDateById(availableDateById);
        availableDate.updateDate(startDate, endDate, status);
        return availableDate;
    }

    public AvailableDate getAvailableDateById(Long availableDateById) {
        return availableDateRepository.findByAvailableDateId(availableDateById)
                .orElseThrow(() -> new AvailableDateCustomException(AvailableDateErrorCode.NOT_FOUND_AVAILABLE));
    }
}
