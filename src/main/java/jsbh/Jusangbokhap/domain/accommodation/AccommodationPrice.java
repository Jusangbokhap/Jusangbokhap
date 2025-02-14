package jsbh.Jusangbokhap.domain.accommodation;


import jakarta.persistence.Embeddable;
import jsbh.Jusangbokhap.api.accommodation.exception.AccommodationCustomException;
import jsbh.Jusangbokhap.api.accommodation.exception.AccommodationErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class AccommodationPrice {

    public static final Integer MIN_PRICE = 1;
    public static final Integer MAX_PRICE = 10_000_000;

    private Integer price;

    public static AccommodationPrice from(Integer price) {
        return new AccommodationPrice(price);
    }

    public void updatePrice(Integer price) {
        if (price == null) return;
        validateAccommodationPrice(price);
        this.price = price;
    }

    private AccommodationPrice(Integer price) {
        validateAccommodationPrice(price);
        this.price = price;
    }

    private void validateAccommodationPrice(Integer price) {
        if (price < MIN_PRICE || price > MAX_PRICE) {
            throw new AccommodationCustomException(AccommodationErrorCode.INVALID_ACCOMMODATION_PRICE);
        }
    }
}
