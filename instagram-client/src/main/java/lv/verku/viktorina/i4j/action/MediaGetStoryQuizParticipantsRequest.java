package lv.verku.viktorina.i4j.action;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.requests.IGGetRequest;
import com.github.instagram4j.instagram4j.requests.IGPaginatedRequest;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
public class MediaGetStoryQuizParticipantsRequest extends IGGetRequest<MediaGetStoryQuizParticipantsResponse>
        implements IGPaginatedRequest {
    @NonNull
    private String reel_id, quiz_id;
    @Setter
    private String max_id;

    @Override
    public String path() {
        return String.format("media/%s/%s/story_quiz_participants/", reel_id, quiz_id);
    }

    @Override
    public String getQueryString(IGClient client) {
        return mapQueryString("max_id", max_id);
    }

    @Override
    public Class<MediaGetStoryQuizParticipantsResponse> getResponseType() {
        return MediaGetStoryQuizParticipantsResponse.class;
    }

}
