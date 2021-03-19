package lv.verku.viktorina.jdbc.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class Pool extends BaseDto {

    @Setter
    @Getter
    private String question;

    @Setter
    @Getter
    private Long ownerId;
}
