package lv.verku.viktorina.jdbc.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@SuperBuilder
public class Profile extends BaseDto {
    public static final Long MALE_GENDER_ID= 1l;
    public static final Long FEMALE_GENDER_ID= 2l;
    public static final Long UNKNOWN_GENDER_ID= 3l;

    private String username;
    private String fullName;
    private String pictureUrl;
    private Long genderId;
    private LocalDateTime pictureDownloadTime;
}
