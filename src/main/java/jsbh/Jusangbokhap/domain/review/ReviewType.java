package jsbh.Jusangbokhap.domain.review;

import lombok.Getter;

@Getter
public enum ReviewType {
    GUEST_TO_HOST("게스트가 호스트에게 작성한 리뷰"),
    HOST_TO_GUEST("호스트가 게스트에게 작성한 리뷰");

    private final String description;

    ReviewType(String description) {
        this.description = description;
    }
}
