package jsbh.Jusangbokhap.api.availableDate.controller;

import jsbh.Jusangbokhap.api.accommodation.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AvailableDateController {

    private final AccommodationService accommodationService;

//
//    //TODO 분리 예쩡
//    public AccommodationResponse updateAccommodationAvailableDate(Long accommodationId,
//                                                                  AccommodationRequest.UpdateAvailableDate request) {
//        AccommodationResponse accommodation = accommodationService.find(accommodationId);
//
//        AvailableDate availableDate = availableDateService.updateAvailableDate(request.availableDateId(),
//                request.startDate(), request.endDate(), AvailableDateStatus.AVAILABLE);
//
//        accommodation.updateAvailableDate(availableDate);
//
//        return new AccommodationResponse.Update(accommodationId);
//    }
}
