package lv.verku.viktorina.jdbc.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class QuizHashtag {

    @Setter
    @Getter
    private Long quizId;

    @Setter
    @Getter
    private Long hashtagId;
}
