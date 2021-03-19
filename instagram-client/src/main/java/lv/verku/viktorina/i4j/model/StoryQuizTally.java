package lv.verku.viktorina.i4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class StoryQuizTally {

    @JsonProperty("text")
    @Setter
    @Getter
    private String text;

    @JsonProperty("count")
    @Setter
    @Getter
    private Integer count;
}
