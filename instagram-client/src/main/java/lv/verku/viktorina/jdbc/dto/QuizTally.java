package lv.verku.viktorina.jdbc.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class QuizTally extends BaseDto {

    @Setter
    @Getter
    private String text;

    @Setter
    @Getter
    private Long quizId;
}
