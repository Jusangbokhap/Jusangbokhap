package jsbh.Jusangbokhap.domain.search.repository;

import jsbh.Jusangbokhap.api.search.dto.response.SearchKeywordRankResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class SearchRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    // 검색어 저장
    public void saveSearchKeyword(String redisKey, String keyword, Long count) {
        redisTemplate.opsForZSet().add(redisKey, keyword, count);
    }

    // 만료 시간 세팅
    public void setExpiryTime(String redisKey, Long timeout) {
        redisTemplate.expire(redisKey, timeout, TimeUnit.MILLISECONDS);
    }

    // 검색어 갱신 시간(메타데이터) 저장
    public void saveRankUpdatingTime(String redisKey, String field, String time) {
        redisTemplate.opsForHash().put(redisKey, field, time);
    }

    // 레디스 캐시에서 상위 top N 개 키워드 조회
    public List<SearchKeywordRankResponse> getAllSearchKeyword(String redisKey) {
        Set<ZSetOperations.TypedTuple<String>> topKeywords =
                redisTemplate.opsForZSet().reverseRangeWithScores(redisKey, 0, -1);

        if (topKeywords == null) {
            return Collections.emptyList();
        }

        List<SearchKeywordRankResponse> rankList = new ArrayList<>();
        int rank = 1;
        for (ZSetOperations.TypedTuple<String> keyword : topKeywords) {
            String value = keyword.getValue(); // 키워드
            Long count = keyword.getScore().longValue(); // 검색 횟수
            rankList.add(SearchKeywordRankResponse.of(rank++, value, count));
        }

        return rankList;
    }

}