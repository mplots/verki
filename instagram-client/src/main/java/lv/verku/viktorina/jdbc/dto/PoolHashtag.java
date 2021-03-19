package lv.verku.viktorina.jdbc.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class PoolHashtag {

    @Setter
    @Getter
    private Long poolId;

    @Setter
    @Getter
    private Long hashtagId;
}
