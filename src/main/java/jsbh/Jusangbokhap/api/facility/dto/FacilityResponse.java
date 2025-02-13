package jsbh.Jusangbokhap.api.facility.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import jsbh.Jusangbokhap.domain.facility.FacilityCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityResponse {

    private List<Document> documents;
    private Meta meta;
    private FacilityCategory category;

    @Getter
    @Setter
    public static class Meta {
        @JsonProperty("total_count")
        private int totalCount;
    }

    @Getter
    @Setter
    public static class Document {
        @JsonProperty("place_name")
        private String placeName;

        @JsonProperty("category_group_code")
        private String categoryGroupCode;

        @JsonProperty("category_name")
        private String categoryName;

        @JsonProperty("distance")
        private String distance;
    }
}
