package lv.verku.viktorina.service.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lv.verku.viktorina.jdbc.dto.QuizSeriesParticipant;

import java.util.List;
import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
public class Leaderboard {
    private Map<Long, List<QuizSeriesParticipant>> leaders;
    private Long maleCount = 0l;
    private Long femaleCount = 0l;
    private Long unknownCount = 0l;
}
