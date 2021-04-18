package lv.verku.viktorina.web.controller;

import lombok.AllArgsConstructor;
import lv.verku.viktorina.Properties;
import lv.verku.viktorina.service.InstagramService;
import lv.verku.viktorina.web.controller.request.GetDuelParams;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class DuelController {

    private InstagramService instagramService;
    private Properties properties;

    @GetMapping("/")
    public String get(GetDuelParams params, Model model) {

        String hashtag = params.getHashtag();
        if (hashtag == null) {
            hashtag = properties.getHashtags().get(0);
        }

        if(properties.getRateLimiter().tryAcquire() && params.getPull()) {
            instagramService.pull();
        }

        model.addAttribute("duel", instagramService.getDuel(hashtag));
        model.addAttribute("seriesTitle", properties.getSeriesTitle());
        model.addAttribute("enableGoogleAnalytics", params.getGa());
        return "duel";
    }
}