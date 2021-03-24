package lv.verku.viktorina.web.controller.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetQuizSeriesParams {
    private List<String> hashtags = new ArrayList<>();
    private Boolean pull = true;
    private Boolean ga = true;
}
