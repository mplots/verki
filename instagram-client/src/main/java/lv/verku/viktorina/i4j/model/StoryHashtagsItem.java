package lv.verku.viktorina.i4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class StoryHashtagsItem {

    @Setter
    @Getter
    @JsonProperty("hashtag")
    private StoryHashtag hashtag;
}
