package lv.verku.viktorina.jdbc.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class Voter extends BaseDto{

    @Setter
    @Getter
    private Long poolTallyId;

    @Setter
    @Getter
    private Long profileId;

}
