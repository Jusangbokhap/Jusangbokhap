package jsbh.Jusangbokhap.domain.review;

import lombok.Getter;

@Getter
public enum ReviewStatus {
    ACTIVE("등록됨"),
    DELETED("삭제됨"),
    UPDATED("수정됨");

    private final String description;

    ReviewStatus(String description) {
        this.description = description;
    }
}
