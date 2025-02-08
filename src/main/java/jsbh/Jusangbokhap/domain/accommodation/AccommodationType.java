package jsbh.Jusangbokhap.domain.accommodation;

import static jsbh.Jusangbokhap.api.accommodation.exception.AccommodationErrorCode.INVALID_CATEGORY;

import jsbh.Jusangbokhap.api.accommodation.exception.AccommodationCustomException;

public enum AccommodationType {
    HOTEL, MOTEL, HOSTEL, RESORT, APARTMENT, GUESTHOUSE, CAMPING;

    public static AccommodationType from(String accommodationType) {
        try {
            return AccommodationType.valueOf(accommodationType);
        } catch (IllegalArgumentException e) {
            throw new AccommodationCustomException(INVALID_CATEGORY, e);
        }
    }
}
