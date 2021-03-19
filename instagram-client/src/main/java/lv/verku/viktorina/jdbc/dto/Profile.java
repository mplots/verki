package lv.verku.viktorina.jdbc.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class Profile extends BaseDto {
    @Setter
    @Getter
    private String username;

    @Setter
    @Getter
    private String fullName;

    @Setter
    @Getter
    private String pictureUrl;

}
