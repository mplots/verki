package lv.verku.viktorina.i4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class StoryPoolSticker {

    @JsonProperty("poll_id")
    @Setter
    @Getter
    private Long pollId;

    @JsonProperty("question")
    @Setter
    @Getter
    private String question;

    @JsonProperty("tallies")
    @Setter
    @Getter
    private List<StoryPoolTally> tallies;

}
