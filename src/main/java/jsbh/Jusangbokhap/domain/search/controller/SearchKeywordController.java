package jsbh.Jusangbokhap.domain.search.controller;

import jsbh.Jusangbokhap.api.search.dto.request.SearchKeywordRequest;
import jsbh.Jusangbokhap.api.search.dto.response.SearchKeywordRankResponse;
import jsbh.Jusangbokhap.api.search.dto.response.SearchKeywordResponse;
import jsbh.Jusangbokhap.api.search.service.SearchKeywordRedisService;
import jsbh.Jusangbokhap.api.search.service.SearchKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/search")
public class SearchKeywordController {

    private final SearchKeywordService keywordService;
    private final SearchKeywordRedisService keywordRedisService;

    @PostMapping
    public SearchKeywordResponse search(@RequestBody SearchKeywordRequest keywordRequest) throws IOException {
        return keywordService.saveKeyword(keywordRequest);
    }

    @GetMapping("/rank")
    public List<SearchKeywordRankResponse> searchKeywordRank() {
        return keywordRedisService.getTopKeywordFromRedis();
    }


}
