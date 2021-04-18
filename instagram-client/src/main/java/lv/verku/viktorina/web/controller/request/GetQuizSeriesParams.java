package lv.verku.viktorina.web.controller.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GetQuizSeriesParams extends BaseParams {
    private List<String> hashtags = new ArrayList<>();
}
