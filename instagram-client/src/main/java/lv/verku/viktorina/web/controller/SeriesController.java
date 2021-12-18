package lv.verku.viktorina.web.controller;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lv.verku.viktorina.Properties;
import lv.verku.viktorina.jdbc.dto.QuizSeriesParticipant;
import lv.verku.viktorina.service.InstagramService;
import lv.verku.viktorina.service.model.Leaderboard;
import lv.verku.viktorina.web.controller.request.GetQuizSeriesParams;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
@Log4j2
public class SeriesController {

    private InstagramService instagramService;
    private Properties properties;

    @GetMapping("/")
    private String getSeries(GetQuizSeriesParams params, Model model, Leaderboard leaderboard) {
        List<String> hashtags = params.getHashtags();
        if (hashtags.size() == 0) {
            hashtags = properties.getHashtags();
        }
        if(properties.getRateLimiter().tryAcquire() && params.getPull()) {
            try {
                instagramService.pull();
            }catch (RuntimeException e) {
                model.addAttribute("pullFailed", true);
            }
        }

        model.addAttribute("leaderboard", instagramService.getQuizSeriesLeaderboard(hashtags));
        model.addAttribute("seriesTitle", properties.getSeriesTitle());
        model.addAttribute("enableGoogleAnalytics", params.getGa());
        return "series";
    }

    @GetMapping("/synchronizeProfiles")
    public String synchronizeProfiles(Model model) {
        try {
            instagramService.synchronizeProfilePictures();
            model.addAttribute("message", "Task launched!");
        } catch (TaskRejectedException e) {
            model.addAttribute("message", "Task already running!");
        }

        return "synchronize";
    }

    @GetMapping("/grid")
    public String grid(GetQuizSeriesParams params, Model model) {

        List<String> hashtags = params.getHashtags();
        if (hashtags.size() == 0) {
            hashtags = properties.getHashtags();
        }


        Leaderboard leaderboard = instagramService.getPoolSeriesLeaderboard(hashtags);
        List<QuizSeriesParticipant> firstPlace = leaderboard.getLeaders().get(1l);
        List<List<QuizSeriesParticipant>> result =  Lists.partition(firstPlace, 2);

        model.addAttribute("result", result);
        model.addAttribute("currentTimeMillis", leaderboard.getCurrentTimeMillis());

        return "grid";
    }
}