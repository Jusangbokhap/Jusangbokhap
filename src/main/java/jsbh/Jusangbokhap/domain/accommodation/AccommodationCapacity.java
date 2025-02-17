package jsbh.Jusangbokhap.domain.accommodation;

import static jsbh.Jusangbokhap.api.accommodation.exception.AccommodationErrorCode.INVALID_GUEST_COUNT;

import jakarta.persistence.Embeddable;
import jsbh.Jusangbokhap.api.accommodation.exception.AccommodationCustomException;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class AccommodationCapacity {

    public static final int MIN_PERSON = 1;
    public static final int MAX_PERSON = 100;

    private Integer maxGuest;

    public AccommodationCapacity(int guest) {
        validateGuest(guest);
        this.maxGuest = guest;
    }

    public static AccommodationCapacity from(Integer guest) {
        return new AccommodationCapacity(guest);
    }

    public void updateMaxGuests(Integer guest) {
        if (guest == null) {
            throw new AccommodationCustomException(INVALID_GUEST_COUNT);
        }
        validateGuest(guest);
        this.maxGuest = guest;
    }

    private void validateGuest(int guest) {
        if (guest < MIN_PERSON || guest > MAX_PERSON) {
            throw new AccommodationCustomException(INVALID_GUEST_COUNT);
        }
    }
}
