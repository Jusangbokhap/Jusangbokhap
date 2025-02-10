package jsbh.Jusangbokhap.domain.search.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.zset.Aggregate;
import org.springframework.data.redis.connection.zset.Weights;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class SearchRedisRepository {

    private final ZSetOperations<String, String> zSetOperations;

    // 검색어 저장
    public void saveSearchKeyword(String key, String keyword) {
        zSetOperations.incrementScore(key, keyword, 1);
    }

    // 검색어 삭제
    public void deleteSearchKeyword(String key, String keyword) {
        zSetOperations.remove(key, keyword);
    }

    // 검색어 합산
    // 1분 전 데이터에 가중치를 2, 2분 전 데이터에 가중치를 1 줌 (시간 가중치)
    public void unionAndStoreKeyword(String oneMinuteKey, String twoMinuteKey, String unitedKeys) {
        zSetOperations.unionAndStore(oneMinuteKey, List.of(twoMinuteKey), unitedKeys,
                Aggregate.SUM, Weights.of(2, 1));
    }

    // key에 해당하는 모든 value 조회
    public Set<String> getAllSearchKeyword(String key) {
        return zSetOperations.range(key, 0, -1);
    }

    // 검색어 상위 n 개 조회
    public Set<String> getTopNSearchKeyword(String key, int limit) {
        return zSetOperations.reverseRange(key, 0, limit - 1);
    }

    // 검색어의 검색 횟수 조회 (검색어 -> 검색 횟수)
    public Double getSearchKeywordScore(String key, String keyword) {
        return zSetOperations.score(key, keyword);
    }

}
