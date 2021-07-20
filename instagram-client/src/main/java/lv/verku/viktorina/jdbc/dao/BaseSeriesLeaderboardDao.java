package lv.verku.viktorina.jdbc.dao;

import lv.verku.viktorina.jdbc.dto.QuizSeriesParticipant;

import java.util.List;

public abstract class BaseSeriesLeaderboardDao {
    public abstract List<QuizSeriesParticipant> get(List<String> hashtags);
}
