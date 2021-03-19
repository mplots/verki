package lv.verku.viktorina.jdbc.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class Hashtag extends BaseDto {

    @Setter
    @Getter
    private String hashtag;
}
