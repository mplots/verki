package lv.verku.viktorina.i4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.instagram4j.instagram4j.models.user.Profile;
import lombok.Getter;
import lombok.Setter;

public class StoryQuizParticipant {

    @JsonProperty("user")
    @Setter
    @Getter
    private Profile user;

    @JsonProperty("answer")
    @Setter
    @Getter
    private Integer answer;

    @JsonProperty("ts")
    @Setter
    @Getter
    private Integer ts;

}
