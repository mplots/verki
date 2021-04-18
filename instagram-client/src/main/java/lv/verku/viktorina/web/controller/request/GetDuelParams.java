package lv.verku.viktorina.web.controller.request;

import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.Data;

@Data
public class GetDuelParams extends BaseParams {
    private String hashtag;

}
