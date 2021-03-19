package lv.verku.viktorina.i4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class StoryQuizSticker {

    @JsonProperty("id")
    @Setter
    @Getter
    private String id;

    @JsonProperty("quiz_id")
    @Setter
    @Getter
    private Long quizId;

    @JsonProperty("question")
    @Setter
    @Getter
    private String question;

    @JsonProperty("correct_answer")
    @Setter
    @Getter
    private Integer correctAnswer;

    @JsonProperty("finished")
    @Setter
    @Getter
    private Boolean finished;

    @JsonProperty("tallies")
    @Setter
    @Getter
    private List<StoryQuizTally> tallies;

}
