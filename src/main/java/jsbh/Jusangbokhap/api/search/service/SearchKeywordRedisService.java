package jsbh.Jusangbokhap.api.search.service;

import jsbh.Jusangbokhap.api.search.dto.response.SearchKeywordRankResponse;
import jsbh.Jusangbokhap.domain.search.repository.SearchRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchKeywordRedisService {

    private final SearchRedisRepository searchRedisRepository;

    @Value("${redis.search.rank}")
    private String RANK_REDIS_KEY;
    @Value("${redis.search.meta}")
    private String META_REDIS_KEY;
    @Value("${redis.search.meta-last-updated}")
    private String META_LAST_UPDATED_FIELD;


    public void storeTopKeywordsAndMetadata(List<SearchKeywordRankResponse> topKeywords,
                                            LocalDateTime currentTime,
                                            Long timeout) {

        // 키워드 저장
        for (SearchKeywordRankResponse topKeyword : topKeywords) {
            searchRedisRepository.saveSearchKeyword(RANK_REDIS_KEY, topKeyword.getKeyword(), topKeyword.getCount());
        }

        // 만료시간 세팅
        searchRedisRepository.setExpiryTime(RANK_REDIS_KEY, timeout);

        // 검색어 갱신 시간 저장
        searchRedisRepository.saveRankUpdatingTime(META_REDIS_KEY,
                META_LAST_UPDATED_FIELD,
                currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }

    public List<SearchKeywordRankResponse> getTopKeywordFromRedis() {
        return searchRedisRepository.getAllSearchKeyword(RANK_REDIS_KEY);
    }

}