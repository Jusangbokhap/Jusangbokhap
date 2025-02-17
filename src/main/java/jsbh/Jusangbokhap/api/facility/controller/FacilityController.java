package jsbh.Jusangbokhap.api.facility.controller;

import java.util.Map;
import jsbh.Jusangbokhap.api.facility.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/facility")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping("/{accommodationId}")
    public Map<String, Integer> getFacility(@PathVariable Long accommodationId) {
        return facilityService.getUpdatedFacilityCounts(accommodationId);
    }
}
