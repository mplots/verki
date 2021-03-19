package lv.verku.viktorina.web.controller;

import com.google.common.util.concurrent.RateLimiter;
import lombok.AllArgsConstructor;
import lv.verku.viktorina.Properties;
import lv.verku.viktorina.i4j.client.InstagramService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
public class QuizSeriesController {

    private final RateLimiter rateLimiter = RateLimiter.create(1.0/120);
    private InstagramService instagramService;
    private Properties properties;

    @GetMapping("/")
    public String get(@RequestParam(name="hastag", required=false) List<String> hashtags,
                      Model model) {

        if (hashtags == null || hashtags.size() == 0) {
            hashtags = properties.getHashtags();
        }

        if(properties.getRateLimiter().tryAcquire()) {
            instagramService.pull();
        }

        model.addAttribute("leaderboard", instagramService.getQuizSeriesLeaderboard(hashtags));
        model.addAttribute("seriesTitle", properties.getSeriesTitle());
        return "index";
    }
}
