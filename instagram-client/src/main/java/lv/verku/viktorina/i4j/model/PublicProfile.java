package lv.verku.viktorina.i4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PublicProfile {

    @JsonProperty("data")
    private Graphql graphql;

    @Data
    public static class Graphql {
        private User user;
    }

    @Data
    public static class User {
        @JsonProperty("profile_pic_url")
        private String profilePicUrl;

        @JsonProperty("id")
        private String id;

        @JsonProperty("username")
        private String username;
    }
}
