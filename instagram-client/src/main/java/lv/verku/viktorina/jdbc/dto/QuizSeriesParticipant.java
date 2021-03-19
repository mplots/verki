package lv.verku.viktorina.jdbc.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class QuizSeriesParticipant extends Profile {

    @Setter
    @Getter
    private Long correctAnswers;

    @Setter
    @Getter
    private Long place;
}
