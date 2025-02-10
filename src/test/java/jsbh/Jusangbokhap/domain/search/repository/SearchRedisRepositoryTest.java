package jsbh.Jusangbokhap.domain.search.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SearchRedisRepositoryTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SearchRedisRepository searchRedisRepository;

    private static final String TEST_KEY = "test-key";

    @AfterEach
    void clear() {
        redisTemplate.delete(TEST_KEY);
    }

    @Test
    @DisplayName("데이터베이스에 이미 키워드가 존재하면 점수가 증가한다.")
    void saveSearchKeyword() {
        searchRedisRepository.saveSearchKeyword(TEST_KEY, "양평");
        searchRedisRepository.saveSearchKeyword(TEST_KEY, "양평");
        searchRedisRepository.saveSearchKeyword(TEST_KEY, "양평");

        Double score = searchRedisRepository.getSearchKeywordScore(TEST_KEY, "양평");
        assertThat(score).isEqualTo(3.0);
    }

    @Test
    @DisplayName("특정 키워드를 삭제할 수 있다.")
    void deleteSearchKeyword() {

        // given
        searchRedisRepository.saveSearchKeyword(TEST_KEY, "양평");

        // when
        searchRedisRepository.deleteSearchKeyword(TEST_KEY, "양평");

        // then
        Set<String> allSearchKeyword = searchRedisRepository.getAllSearchKeyword(TEST_KEY);
        assertEquals(0, allSearchKeyword.size());
    }

    @Test
    @DisplayName("많이 검색된 순으로 결과가 출력된다.")
    void getTopNSearchKeyword() {
        searchRedisRepository.saveSearchKeyword(TEST_KEY, "Spring");
        searchRedisRepository.saveSearchKeyword(TEST_KEY, "Java");
        searchRedisRepository.saveSearchKeyword(TEST_KEY, "Redis");
        searchRedisRepository.saveSearchKeyword(TEST_KEY, "MySQL");
        searchRedisRepository.saveSearchKeyword(TEST_KEY, "Spring");

        Set<String> topNSearchKeyword = searchRedisRepository.getTopNSearchKeyword(TEST_KEY, 5);

        assertThat(topNSearchKeyword.size()).isEqualTo(4);
        assertThat(topNSearchKeyword).containsExactly("Spring", "Redis", "MySQL", "Java");
    }

    @Test
    @DisplayName("1분 전 데이터와 2분 전 데이터를 합산하여 가중치를 적용한다.")
    void getUnitedKey() {

        // given
        String beforeOneMinuteKey = "realtime-keywords-0900"; // 9시00분 (2분전)
        String beforeTwoMinuteKey = "realtime-keywords-0901"; // 9시01분 (1분전)
        String resultKey = "realtime-keyword-result-0902";

        searchRedisRepository.saveSearchKeyword(beforeOneMinuteKey, "Spring");
        searchRedisRepository.saveSearchKeyword(beforeOneMinuteKey, "Redis");
        searchRedisRepository.saveSearchKeyword(beforeOneMinuteKey, "MySQL");
        searchRedisRepository.saveSearchKeyword(beforeOneMinuteKey, "MySQL");
        searchRedisRepository.saveSearchKeyword(beforeTwoMinuteKey, "Java");
        searchRedisRepository.saveSearchKeyword(beforeTwoMinuteKey, "Spring");

        // when
        searchRedisRepository.unionAndStoreKeyword(beforeOneMinuteKey, beforeTwoMinuteKey, resultKey);

        // then
        Set<String> topNSearchKeyword = searchRedisRepository.getTopNSearchKeyword(resultKey, 10);
        assertNotNull(topNSearchKeyword);
        assertEquals(4, topNSearchKeyword.size());
        assertThat(topNSearchKeyword).containsExactly("MySQL", "Spring", "Redis", "Java");
    }

}