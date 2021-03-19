package lv.verku.viktorina.i4j.client;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.models.feed.Reel;
import com.github.instagram4j.instagram4j.models.media.reel.ReelMedia;
import com.github.instagram4j.instagram4j.models.media.reel.VoterInfo;
import com.github.instagram4j.instagram4j.models.user.Profile;
import com.github.instagram4j.instagram4j.requests.media.MediaGetStoryPollVotersRequest;
import com.github.instagram4j.instagram4j.utils.IGUtils;

import lombok.SneakyThrows;
import lv.verku.viktorina.i4j.action.MediaGetStoryQuizParticipantsRequest;
import lv.verku.viktorina.i4j.model.*;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Client {

    private IGClient client;
    final ObjectMapper mapper = new ObjectMapper();


    @SneakyThrows
    public Client(
                @Value("#{environment.IG_USERNAME}") String username,
                @Value("#{environment.IG_PASSWORD}") String password,
                @Value("#{environment.IG_DATA_DIR ?: '/tmp'}") String dataDir
            ) {


        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        File clientFile = new File(dataDir + "/" + username + ".client.ser");
        File cookieFile = new File(dataDir + "/" + username + ".cookie.ser");
        if (!clientFile.exists() || !cookieFile.exists()) {
            client = serializeLogin(clientFile, cookieFile, username, password);
        } else {
            client = getClientFromSerialize(clientFile, cookieFile);
        }
    }

    public List<ReelMediaWrapper> getReelMedia() {
        List<ReelMediaWrapper> allMedia = getBaseReelMedia();
        for (ReelMediaWrapper media : allMedia) {
            ReelExtraProperties properties = media.getReelExtraProperties();

            if (media.getReelExtraProperties().getStoryPools() != null) {
                for(StoryPool storyPool : properties.getStoryPools()) {
                    String poolId = String.valueOf(storyPool.getPoolSticker().getPollId());

                    List<VoterInfo> storyPollVoterInfos = properties.getStoryPollVoterInfos();
                    if (storyPollVoterInfos.size() > 0) {
                        VoterInfo voterInfo = storyPollVoterInfos.get(storyPollVoterInfos.size() - 1);
                        getNextStoryPollVoters(properties.getStoryPollVoterInfos(), media.getReelMedia().getId(), poolId, voterInfo.getMax_id());
                    }

                }
            }

            if (media.getReelExtraProperties().getStoryQuizs() != null) {
                for(StoryQuiz quiz : media.getReelExtraProperties().getStoryQuizs()) {
                    String quizId = String.valueOf(quiz.getQuizSticker().getQuizId());
                    List<StoryParticipantInfo> storyQuizParticipantInfos = properties.getStoryQuizParticipantInfos();
                    if (storyQuizParticipantInfos.size() > 0) {
                        StoryParticipantInfo participantInfo = storyQuizParticipantInfos.get(storyQuizParticipantInfos.size()-1);
                        getNextQuizParticipants(properties.getStoryQuizParticipantInfos(), media.getReelMedia().getId(), quizId, participantInfo.getMaxId());
                    }
                }
            }
        }

        return allMedia;
    }

    private void getNextStoryPollVoters(List<VoterInfo> voterInfos, String reelId, String poolId, String maxId) {
        if (maxId == null) {
            return;
        }
        new MediaGetStoryPollVotersRequest(reelId, poolId, maxId).execute(client).thenAccept(r->{
            voterInfos.add(r.getVoter_info());
            getNextStoryPollVoters(voterInfos, reelId, poolId, r.getNext_max_id());

        })
        .exceptionally(e->{
            throw new RuntimeException(e);
        })
        .join();
    }

    private void getNextQuizParticipants(List<StoryParticipantInfo> participantInfos, String reelId, String quizId, String maxId ) {
        if (maxId == null) {
            return;
        }
        new MediaGetStoryQuizParticipantsRequest(reelId, quizId, maxId).execute(client).thenAccept(r->{
            participantInfos.add(r.getParticipant_info());
            getNextQuizParticipants(participantInfos, reelId, quizId, maxId);

        })
        .exceptionally(e->{
            throw new RuntimeException(e);
        })
        .join();
    }


    @SneakyThrows
    private List<ReelMediaWrapper> getBaseReelMedia() {

        List<ReelMediaWrapper> allProperties = new ArrayList<>();
        Profile me = client.getSelfProfile();
        client.actions()
                .story()
                .tray()
                .thenAccept(feedReelsTrayResponse -> {

                    Optional<Reel> reel = feedReelsTrayResponse.getTray().stream().filter(
                            i -> Objects.equals(i.getUser().getPk(), me.getPk())
                    ).findFirst();

                    if (reel.isPresent()) {
                        for (ReelMedia reelItem : reel.get().getItems()) {
                            Map<String, Object> extraProperties = reelItem.getExtraProperties();

                            ReelExtraProperties properties;
                            properties = mapper.convertValue(extraProperties, ReelExtraProperties.class);
                            allProperties.add(new ReelMediaWrapper(me, reelItem, properties, reel.get().getId()));
                        }
                    }
                }).exceptionally(e->{
                    throw new RuntimeException(e);
                }).join();

        return allProperties;
    }


    @SneakyThrows
    private IGClient serializeLogin(File clientFile, File cookieFile, String username, String password) {
        SerializableCookieJar jar = new SerializableCookieJar();
        IGClient lib = new IGClient.Builder().username(username).password(password)
                .client(formTestHttpClient(jar))
                .login();
        serialize(lib, clientFile);
        serialize(jar, cookieFile);
        return IGClient.from(new FileInputStream(clientFile), formTestHttpClient(deserialize(cookieFile, SerializableCookieJar.class)));
    }

    @SneakyThrows
    private IGClient getClientFromSerialize(File clientFile, File cookieFile) {

        InputStream fileIn = new FileInputStream(clientFile);
        IGClient client = IGClient.from(fileIn,
                formTestHttpClient(deserialize(cookieFile, SerializableCookieJar.class)));
        fileIn.close();

        return client;
    }

    @SneakyThrows
    private void serialize(Object o, File to) {
        FileOutputStream file = new FileOutputStream(to);
        ObjectOutputStream out = new ObjectOutputStream(file);

        out.writeObject(o);
        out.close();
        file.close();
    }

    @SneakyThrows
    private static <T> T deserialize(File file, Class<T> clazz) {
        InputStream in = new FileInputStream(file);
        ObjectInputStream oIn = new ObjectInputStream(in);

        T t = clazz.cast(oIn.readObject());

        in.close();
        oIn.close();

        return t;
    }

    public static OkHttpClient formTestHttpClient(SerializableCookieJar jar) {
        return IGUtils.defaultHttpClientBuilder().cookieJar(jar).build();
    }
}
