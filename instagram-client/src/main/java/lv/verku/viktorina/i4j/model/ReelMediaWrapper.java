package lv.verku.viktorina.i4j.model;

import com.github.instagram4j.instagram4j.models.media.reel.ReelMedia;
import com.github.instagram4j.instagram4j.models.user.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class ReelMediaWrapper {

    @Setter
    @Getter
    private Profile owner;

    @Setter
    @Getter
    private ReelMedia reelMedia;

    @Setter
    @Getter
    private ReelExtraProperties reelExtraProperties;

    @Setter
    @Getter
    private String reelId;
}
