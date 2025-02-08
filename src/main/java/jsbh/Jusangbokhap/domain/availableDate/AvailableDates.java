package jsbh.Jusangbokhap.domain.availableDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jsbh.Jusangbokhap.api.availableDate.exception.AvailableDateCustomException;
import jsbh.Jusangbokhap.api.availableDate.exception.AvailableDateErrorCode;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;

public class AvailableDates {

    private final List<AvailableDate> availableDates;

    public AvailableDates(List<AvailableDate> availableDates) {
        if (availableDates != null) {
            this.availableDates = availableDates;
            return;
        }
        this.availableDates = new ArrayList<>();
    }

    public List<AvailableDate> getDates() {
        return Collections.unmodifiableList(availableDates);
    }

    public void add(AvailableDate newDate, Accommodation accommodation) {
        validateDateOverlap(newDate);
        newDate.setAccommodation(accommodation);
        availableDates.add(newDate);
    }

    public void update(AvailableDate updatedDate) {
        validateDateOverlap(updatedDate);

        for (int i = 0; i < availableDates.size(); i++) {
            AvailableDate current = availableDates.get(i);
            if (current.getId().equals(updatedDate.getId())) {
                availableDates.set(i, updatedDate);
                return;
            }
        }
        throw new AvailableDateCustomException(AvailableDateErrorCode.NOT_FOUND_AVAILABLE);
    }

    public void validateDateOverlap(AvailableDate newDate) {
        for (AvailableDate existingDate : availableDates) {
            if (existingDate.getId().equals(newDate.getId())) {
                continue;
            }

            if (existingDate.isOverlappingWith(newDate)) {
                throw new AvailableDateCustomException(AvailableDateErrorCode.DATE_OVERLAP);
            }
        }
    }

}
