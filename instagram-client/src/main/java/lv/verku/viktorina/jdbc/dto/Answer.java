package lv.verku.viktorina.jdbc.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class Answer extends BaseDto {

    @Setter
    @Getter
    private Long quizTallyId;

    @Setter
    @Getter
    private Long profileId;

}
