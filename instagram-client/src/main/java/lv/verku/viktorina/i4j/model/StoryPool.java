package lv.verku.viktorina.i4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class StoryPool {

    @JsonProperty("poll_sticker")
    @Setter
    @Getter
    private StoryPoolSticker poolSticker;
}
