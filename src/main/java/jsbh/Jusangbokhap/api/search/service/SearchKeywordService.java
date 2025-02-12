package jsbh.Jusangbokhap.api.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import jsbh.Jusangbokhap.api.search.dto.request.SearchKeywordRequest;
import jsbh.Jusangbokhap.api.search.dto.response.SearchKeywordResponse;
import jsbh.Jusangbokhap.api.search.dto.response.SearchKeywordRankResponse;
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

    // 검색 시, 키워드 저장
    public SearchKeywordResponse saveKeyword(SearchKeywordRequest keywordRequest) throws IOException {

        // will add try-catch exception
        KeywordDocument keywordDocument = new KeywordDocument(keywordRequest.getKeyword());

        IndexResponse response = esClient.index(i -> i
                .index(SEARCH_INDEX_NAME)
                .document(keywordDocument)
        );

        return SearchKeywordResponse.of(response.id(), keywordDocument);
    }

    // 상위 검색어 반환 (집계, Aggregation 사용)
    public List<SearchKeywordRankResponse> getTopNKeywords(int n) throws IOException {

        // will add try-catch exception
        SearchResponse<Void> response = esClient.search(s -> s
                        .index(SEARCH_INDEX_NAME)
                        .size(0) // 집계 결과만 필요하므로 검색 결과는 받아오지 않음
                        .aggregations(SEARCH_AGGS_RESULT_NAME, a -> a
                                .terms(t -> t
                                        .field("keyword.keyword")
                                        .size(n) // 상위 n개 키워드 반환
                                )
                        ),
                Void.class
        );

        List<SearchKeywordRankResponse> rankList = new ArrayList<>();
        StringTermsAggregate termsAggregate = response.aggregations().get(SEARCH_AGGS_RESULT_NAME).sterms();
        int rank = 1;
        for (StringTermsBucket bucket : termsAggregate.buckets().array()) {
            String key = bucket.key().stringValue();
            long docCount = bucket.docCount();

            rankList.add(SearchKeywordRankResponse.of(rank++, key, docCount));
        }

        return rankList;
    }



}
