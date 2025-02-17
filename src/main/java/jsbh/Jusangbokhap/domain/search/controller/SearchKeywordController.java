package jsbh.Jusangbokhap.domain.search.controller;

import jsbh.Jusangbokhap.api.search.dto.response.SearchKeywordRankResponse;
import jsbh.Jusangbokhap.api.search.service.SearchKeywordRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/search")
public class SearchKeywordController {

    private final SearchKeywordRedisService keywordRedisService;

    @GetMapping("/rank")
    public List<SearchKeywordRankResponse> searchKeywordRank() {
        return keywordRedisService.getTopKeywordFromRedis();
    }

}
