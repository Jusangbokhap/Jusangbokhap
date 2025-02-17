package jsbh.Jusangbokhap.api.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import jsbh.Jusangbokhap.api.accommodation.dto.AccommodationRequest;
import jsbh.Jusangbokhap.api.search.dto.response.SearchKeywordResponse;
import jsbh.Jusangbokhap.api.search.dto.response.SearchKeywordRankResponse;
import jsbh.Jusangbokhap.common.exception.CustomException;
import jsbh.Jusangbokhap.common.exception.ErrorCode;
import jsbh.Jusangbokhap.domain.search.document.KeywordDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
public class SearchKeywordService {

    @Value("${elasticsearch.custom.search.index-name}")
    private String SEARCH_INDEX_NAME;
    @Value("${elasticsearch.custom.search.top-index-name}")
    private String SEARCH_AGGS_RESULT_NAME;
    private final ElasticsearchClient esClient;
    private static final int SEARCH_LIMIT = 10;

    // 검색 시, 키워드 저장
    public SearchKeywordResponse saveKeyword(AccommodationRequest.Search search) throws IOException {

        String keyword = getKeyword(search);
        if (keyword == null) {
            throw new CustomException(ErrorCode.NOT_EXIST_KEYWORD);
        }

        KeywordDocument keywordDocument = new KeywordDocument(keyword);

        IndexResponse response = esClient.index(i -> i
                .index(SEARCH_INDEX_NAME)
                .document(keywordDocument)
        );

        return SearchKeywordResponse.of(response.id(), keywordDocument);
    }

    // es 에서 상위 검색어 반환 후 redis 캐시에 저장 (집계, Aggregation 사용)
    public List<SearchKeywordRankResponse> getTopNKeywords() throws IOException {

        // will add try-catch exception
        SearchResponse<Void> response = esClient.search(s -> s
                        .index(SEARCH_INDEX_NAME)
                        .size(0) // 집계 결과만 필요하므로 검색 결과는 받아오지 않음
                        .aggregations(SEARCH_AGGS_RESULT_NAME, a -> a
                                .terms(t -> t
                                        .field("keyword.keyword")
                                        .size(SEARCH_LIMIT) // 상위 SEARCH_LIMIT 개 키워드 반환
                                )
                        ),
                Void.class
        );

        List<SearchKeywordRankResponse> rankList = new ArrayList<>();
        StringTermsAggregate termsAggregate = response.aggregations().get(SEARCH_AGGS_RESULT_NAME).sterms();

        for (StringTermsBucket bucket : termsAggregate.buckets().array()) {
            String key = bucket.key().stringValue();
            long docCount = bucket.docCount();
            rankList.add(SearchKeywordRankResponse.of(key, docCount));
        }

        return rankList;
    }

    // 상호명, 읍면동, 시군구, 시도 순서로 키워드를 가져온다.
    private String getKeyword(AccommodationRequest.Search search) {

        String businessName = search.businessName(); // 상호명
        String eupmyeondong = search.eupmyeondong();// 읍면동
        String sigungu = search.sigungu();// 시군구
        String sido = search.sido();// 시도

        if (businessName != null && !businessName.trim().isEmpty()) {
            return businessName;
        }

        if (eupmyeondong != null && !eupmyeondong.trim().isEmpty()) {
            return eupmyeondong;
        }

        if (sigungu != null && !sigungu.trim().isEmpty()) {
            return sigungu;
        }

        if (sido != null && !sido.trim().isEmpty()) {
            return sido;
        }

        return null;
    }



}
