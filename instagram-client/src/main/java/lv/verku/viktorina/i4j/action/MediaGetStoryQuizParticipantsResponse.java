package lv.verku.viktorina.i4j.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.instagram4j.instagram4j.responses.IGPaginatedResponse;
import com.github.instagram4j.instagram4j.responses.IGResponse;
import lv.verku.viktorina.i4j.model.StoryParticipantInfo;

public class MediaGetStoryQuizParticipantsResponse extends IGResponse implements IGPaginatedResponse {

    @JsonProperty
    private StoryParticipantInfo participant_info;

    @Override
    public String getNext_max_id() {
        return participant_info.getMaxId();
    }

    @Override
    public boolean isMore_available() {
        return participant_info.getMoreAvailable();
    }

    public StoryParticipantInfo getParticipant_info() {
        return participant_info;
    }
}
