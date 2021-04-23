package lv.verku.viktorina.web.controller;

import lombok.AllArgsConstructor;
import lv.verku.viktorina.Properties;
import lv.verku.viktorina.service.InstagramService;
import lv.verku.viktorina.web.controller.request.GetQuizSeriesParams;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
public class QuizSeriesController {

    private InstagramService instagramService;
    private Properties properties;

    @GetMapping("/")
    public String get(GetQuizSeriesParams params, Model model) {

        List<String> hashtags = params.getHashtags();
        if (hashtags.size() == 0) {
            hashtags = properties.getHashtags();
        }

        if(properties.getRateLimiter().tryAcquire() && params.getPull()) {
            instagramService.pull();
        }


        model.addAttribute("leaderboard", instagramService.getQuizSeriesLeaderboard(hashtags));
        model.addAttribute("seriesTitle", properties.getSeriesTitle());
        model.addAttribute("enableGoogleAnalytics", params.getGa());
        return "quiz";
    }

    @GetMapping("/synchronizeProfiles")
    public String synchronizeProfiles() {
        instagramService.synchronizeProfilePictures();
        return "empty";
    }

}
