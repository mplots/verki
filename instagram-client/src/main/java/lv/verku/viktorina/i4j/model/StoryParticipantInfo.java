package lv.verku.viktorina.i4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class StoryParticipantInfo {

    @JsonProperty("quiz_id")
    @Setter
    @Getter
    private Long quizId;

    @JsonProperty("participants")
    @Setter
    @Getter
    private List<StoryQuizParticipant> participants;

    @JsonProperty("max_id")
    @Setter
    @Getter
    private String maxId;

    @JsonProperty("more_available")
    @Setter
    @Getter
    private Boolean moreAvailable;

}
