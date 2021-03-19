package lv.verku.viktorina.jdbc.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class PoolTally extends BaseDto {

    @Setter
    @Getter
    private String text;

    @Setter
    @Getter
    private Long poolId;
}
