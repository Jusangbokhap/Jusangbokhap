package jsbh.Jusangbokhap.api.facility.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jsbh.Jusangbokhap.api.accommodation.service.AccommodationHostService;
import jsbh.Jusangbokhap.api.facility.dto.FacilityResponse;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import jsbh.Jusangbokhap.domain.facility.Facility;
import jsbh.Jusangbokhap.domain.facility.FacilityCategory;
import jsbh.Jusangbokhap.domain.facility.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacilityService {

    public static final int DEFAULT_UPDATE_CYCLE = 7;
    public static final int DEFAULT_RADIUS = 1_000;

    private final WebClient webClient;
    private final FacilityRepository facilityRepository;
    private final AccommodationHostService hostService;

    @Transactional
    public Map<String, Integer> getUpdatedFacilityCounts(Long accommodationId) {

        Accommodation accommodation = hostService.getAccommodationByAccommodationId(accommodationId);
        List<Facility> existingFacilities = facilityRepository.findByAccommodationId(accommodationId);

        List<FacilityCategory> categoriesToUpdate = determineCategoriesToUpdate(existingFacilities);

        if (!categoriesToUpdate.isEmpty()) {
            List<Facility> updatedFacilities = updateFacilitiesForAccommodation(accommodation, categoriesToUpdate, DEFAULT_RADIUS);
            List<Facility> mergedFacilities = mergeFacilitiesForUpdate(existingFacilities, updatedFacilities);
            facilityRepository.saveAll(mergedFacilities);
            existingFacilities = mergedFacilities;
        }

        return mapFacilitiesToCounts(existingFacilities);
    }

    private List<Facility> mergeFacilitiesForUpdate(List<Facility> existing, List<Facility> updated) {
        Map<FacilityCategory, Facility> merged = new HashMap<>();

        for (Facility facility : existing) {
            merged.put(facility.getCategory(), facility);
        }
        for (Facility updatedFacility : updated) {
            if (merged.containsKey(updatedFacility.getCategory())) {
                Facility existingFacility = merged.get(updatedFacility.getCategory());
                existingFacility.updateFacility(updatedFacility.getFacilityCount());
            } else {
                merged.put(updatedFacility.getCategory(), updatedFacility);
            }
        }
        return new ArrayList<>(merged.values());
    }

    private List<FacilityCategory> determineCategoriesToUpdate(List<Facility> facilities) {

        List<FacilityCategory> categories = new ArrayList<>();

        if (facilities.isEmpty()) {
            categories.addAll(Arrays.asList(FacilityCategory.values()));
            return categories;
        }

        for (Facility facility : facilities) {
            if (shouldUpdateFacility(facility)) {
                categories.add(facility.getCategory());
            }
        }

        return categories;
    }

    private List<Facility> updateFacilitiesForAccommodation(Accommodation accommodation,
                                                            List<FacilityCategory> categories, int radius) {
        List<FacilityResponse> responses = fetchNearbyFacilities(
                categories,
                accommodation.getAddress().getLongitude(),
                accommodation.getAddress().getLatitude(),
                radius)
                .collectList()
                .block();

        List<Facility> updatedFacilities = new ArrayList<>();

        if (responses != null) {
            for (FacilityResponse response : responses) {
                Facility facility = Facility.builder()
                        .accommodation(accommodation)
                        .facilityCount(response.getMeta().getTotalCount())
                        .category(response.getCategory())
                        .build();
                updatedFacilities.add(facility);
            }
        }
        return updatedFacilities;
    }

    private Flux<FacilityResponse> fetchNearbyFacilities(List<FacilityCategory> categories, Double longitude,
                                                         Double latitude, int radius) {
        return Flux.fromIterable(categories)
                .flatMap(category -> webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("category_group_code", category)
                                .queryParam("x", longitude)
                                .queryParam("y", latitude)
                                .queryParam("radius", radius)
                                .build())
                        .retrieve()
                        .bodyToMono(FacilityResponse.class)
                        .map(response -> {
                            response.setCategory(category);
                            return response;
                        })
                );
    }

    private boolean shouldUpdateFacility(Facility facility) {
        return LocalDate.now().isAfter(facility.getUpdatedAt().toLocalDate().plusDays(DEFAULT_UPDATE_CYCLE));
    }

    private Map<String, Integer> mapFacilitiesToCounts(List<Facility> facilities) {
        Map<String, Integer> result = new HashMap<>();
        for (Facility facility : facilities) {
            result.put(facility.getCategory().getDescription(), facility.getFacilityCount());
        }
        return result;
    }

}
