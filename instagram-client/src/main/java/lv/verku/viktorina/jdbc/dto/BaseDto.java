package lv.verku.viktorina.jdbc.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class BaseDto {
    @Setter
    @Getter
    private Long id;
}
