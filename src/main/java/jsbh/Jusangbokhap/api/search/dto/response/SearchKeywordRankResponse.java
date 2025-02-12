package jsbh.Jusangbokhap.api.search.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchKeywordRankResponse {

    private int rank;
    private String keyword;
    private Long count; // 검색 횟수

    public static SearchKeywordRankResponse of(int rank, String keyword, Long count) {
        return SearchKeywordRankResponse.builder()
                .rank(rank)
                .keyword(keyword)
                .count(count)
                .build();
    }

}
