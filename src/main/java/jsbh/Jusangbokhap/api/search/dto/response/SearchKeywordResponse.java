package jsbh.Jusangbokhap.api.search.dto.response;

import jsbh.Jusangbokhap.domain.search.document.KeywordDocument;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SearchKeywordResponse {

    private String id; // 후에 삭제할 수 있음
    
    private String keyword;
    
    private LocalDateTime timestamp;

    public static SearchKeywordResponse of(String id, KeywordDocument keywordDocument) {
        return SearchKeywordResponse.builder()
                .id(id)
                .keyword(keywordDocument.getKeyword())
                .timestamp(keywordDocument.getTimestamp())
                .build();
    }
}
