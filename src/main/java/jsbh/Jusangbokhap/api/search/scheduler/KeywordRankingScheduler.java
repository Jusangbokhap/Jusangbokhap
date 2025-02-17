package jsbh.Jusangbokhap.api.search.scheduler;

import jsbh.Jusangbokhap.api.search.dto.response.SearchKeywordRankResponse;
import jsbh.Jusangbokhap.api.search.service.SearchKeywordRedisService;
import jsbh.Jusangbokhap.api.search.service.SearchKeywordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeywordRankingScheduler {

    private final SearchKeywordService searchKeywordService;
    private final SearchKeywordRedisService keywordRedisService;
    private static final long TIMEOUT = 300_000L;

    @Async
    @Scheduled(fixedRate = TIMEOUT)
    public void updateKeywordRanking() {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            log.info("start scheduler: {}", currentTime);

            // 1. ES에서 상위 검색어 데이터를 가져온다.
            List<SearchKeywordRankResponse> topNKeywords = searchKeywordService.getTopNKeywords();

            // 2. 가져온 데이터를 Redis 에 저장한다.
            keywordRedisService.storeTopKeywordsAndMetadata(topNKeywords, currentTime, TIMEOUT);
        } catch(IOException e) {
            log.error("Failed to update keyword ranking: {}", e.getMessage(), e);
        }
    }
}