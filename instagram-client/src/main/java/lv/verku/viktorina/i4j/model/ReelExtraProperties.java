package lv.verku.viktorina.i4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.github.instagram4j.instagram4j.models.media.reel.VoterInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ReelExtraProperties {

    @JsonProperty("story_quiz_participant_infos")
    @Setter
    @Getter
    private List<StoryParticipantInfo> storyQuizParticipantInfos = new ArrayList<>();;

    @JsonProperty("story_quizs")
    @Setter
    @Getter
    private List<StoryQuiz> storyQuizs = new ArrayList<>();;

    @JsonProperty("story_polls")
    @Setter
    @Getter
    private List<StoryPool> storyPools = new ArrayList<>();;

    @JsonProperty("story_poll_voter_infos")
    @Setter
    @Getter
    private List<VoterInfo> storyPollVoterInfos = new ArrayList<>();;

    @JsonProperty("story_hashtags")
    @Setter
    @Getter
    private List<StoryHashtagsItem> storyHashtags = new ArrayList<>();
}
