package lv.verku.viktorina.jdbc.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class DuelParticipant extends Profile{

    @Setter
    @Getter
    private String answer;
}
