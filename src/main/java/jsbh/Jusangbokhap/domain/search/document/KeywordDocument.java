package jsbh.Jusangbokhap.domain.search.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

@Getter
@Document(indexName = "search-keyword")
@Setting(replicas = 0)
public class KeywordDocument {

    @Id
    private String id; // 엘라스틱 서치는 UUID 형식으로 id 를 생성하므로 String 타입이 더 알맞음.

    @Field(type = FieldType.Keyword)
    private String keyword;

    @Field(type = FieldType.Date)
    private LocalDateTime timestamp;

    public KeywordDocument(String keyword) {
        this.keyword = keyword;
        this.timestamp = LocalDateTime.now();
    }
}
