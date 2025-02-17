package jsbh.Jusangbokhap.api.search.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchKeywordRequest {

    @NotEmpty
    private String keyword;

    @Builder
    public SearchKeywordRequest(String keyword) {
        this.keyword = keyword;
    }

}
