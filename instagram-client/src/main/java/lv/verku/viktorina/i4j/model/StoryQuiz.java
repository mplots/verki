package lv.verku.viktorina.i4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StoryQuiz {

    @JsonProperty("quiz_sticker")
    private StoryQuizSticker quizSticker;

    public StoryQuizSticker getQuizSticker() {
        return quizSticker;
    }

    public void setQuizSticker(StoryQuizSticker quizSticker) {
        this.quizSticker = quizSticker;
    }
}
