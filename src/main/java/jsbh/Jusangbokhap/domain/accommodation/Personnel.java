package jsbh.Jusangbokhap.domain.accommodation;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
public class Personnel {

    private Integer maxGuest;

    public Personnel(int guest) {
        validateGuest(guest);
        this.maxGuest = guest;
    }

    private void validateGuest(int guest) {
        if (guest < 1 || guest > 100) {
            throw new RuntimeException("1~100명 사이의 인원만 가능합니다.");
        }
    }
}
