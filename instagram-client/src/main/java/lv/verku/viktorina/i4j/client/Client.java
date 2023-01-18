package lv.verku.viktorina.i4j.client;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
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

import com.google.common.util.concurrent.RateLimiter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import lv.verku.viktorina.i4j.action.MediaGetStoryQuizParticipantsRequest;
import lv.verku.viktorina.i4j.client.exception.ClientException;
import lv.verku.viktorina.i4j.model.*;
import okhttp3.OkHttpClient;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import simplehttp.HttpResponse;

import static java.nio.charset.Charset.forName;
import static simplehttp.HeaderList.headers;
import static simplehttp.HeaderPair.header;
import static simplehttp.HttpClients.anApacheClient;

@Component
@Log4j
public class Client {

    private IGClient client;
    final ObjectMapper mapper = new ObjectMapper();
    private String dataDir;
    private RateLimiter rateLimiter = RateLimiter.create(1.0/15.0);

    @SneakyThrows
    public Client(
                @Value("#{environment.IG_USERNAME}") String username,
                @Value("#{environment.IG_PASSWORD}") String password,
                @Value("#{environment.IG_DATA_DIR ?: '/tmp'}") String dataDir
            ) {

        this.dataDir = dataDir;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        File clientFile = new File(dataDir + "/" + username + ".client.ser");
        File cookieFile = new File(dataDir + "/" + username + ".cookie.ser");
        if (!clientFile.exists() || !cookieFile.exists()) {
            client = serializeLogin(clientFile, cookieFile, username, password);
        } else {
            client = getClientFromSerialize(clientFile, cookieFile);
        }
    }

    public PublicProfile getPublicProfile(String username) throws ClientException {
        try {
            rateLimiter.acquire();
            URL url = new URL("https://www.instagram.com/"+ username +"/?__a=1");
            HttpResponse response = anApacheClient().get(url,
                    headers(
                            header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.128 Safari/537.36"),
                            header("cookie", " ig_cb=1; ig_did=F00D3006-4C93-4F9E-A39A-55FF953A0C7B; mid=Xv8jpgAEAAGKY7BqoYCdP0z_Fk-2; datr=MlqEX4KAhgfOusBGmpfM9YO7; fbm_124024574287414=base_domain=.instagram.com; csrftoken=x310uUdwEqb7VWPwB2IlQ04lICCvfmV7; ds_user_id=33955474; sessionid=33955474%3Awx9Dmc53fsjgri%3A3; shbid=14751; rur=VLL; shbts=1619184601.2082224; fbsr_124024574287414=fubw8adnw6NNSUKYZCZAUGU4_CK8uiFSQj_MR-48qqM.eyJ1c2VyX2lkIjoiMTAwMDAwMzgwODExNDYxIiwiY29kZSI6IkFRQmJMeWhJUmNtV2ZHenhIajQ0ZFo0RUFKYUg1QzlnZ2k1YzVrbnhkSWtUcDNiWVVhWHFIdXZkUFJMUTYxbmFxdjJ3V0txWjRhN1MySUhxTnY1V3hXeHJGR1Z0YlBJeVdmS3UzZHRjbEVrVjZhQ3B5LUttM3BlQnRXeUZiLW9CMzZHMkNzMHpCTHVWYkczQkxleHdNd2FXdUJTX0o0cHVQd0o3VlotWlV1aGtJVVU1MlNlVnJuUzBrdGVjZTA4YTBxck5pdFFER1c1b1owZHgtV3dCVXNDbE5oQjNfcFdXWG9odHlzZWltbV9NRnozNzMzMTJjRHRMekVENVlrS1VzYi1BaloyQmpqR3hUdHBLaWZSMVVWbHpPdUNrQ1U4dU5POU0xZXZxdXRiSks3cHJrVGFpRm80eU1wb3hiVi0zc0cxNV9TZUo1MzJPNFZHa3N5Vktsa2puIiwib2F1dGhfdG9rZW4iOiJFQUFCd3pMaXhuallCQUNQV1EyWkJPMzVoWkFMdXQwblVqeEhaQ3dWSGJRVWFLQzJRZmQ0RzZPTDMySzJhbXdnSzRGdVNvRUR3V2ExOUxHZzdwWkFLUzBUMlNvWXlYRmJCN0ZDSGpyd3lIQ2JzWXZteURJTmRuRVpBQUkzeUZkNUhGZ3BSYnFWQjBNRDNWWkJTZVpCVXZaQ0NDZXpTbEd1TWR4dkpmSUNDTVd5ZTBFbVpDMjV2MDVSNTYiLCJhbGdvcml0aG0iOiJITUFDLVNIQTI1NiIsImlzc3VlZF9hdCI6MTYxOTIwMzcxM30")
                    ));
            String json = response.getContent().asString();
            log.debug(json);
            return mapper.readValue(json, PublicProfile.class);
        }catch (IOException e) {
            throw new ClientException(e);
        }
    }

    public Boolean downloadProfilePicture(String downloadURL, String imagePath, Boolean override) throws ClientException {
        try {
            File imageFile = new File(imagePath);

            if (imageFile.exists() && override) {
                imageFile.delete();
            }

            if (!imageFile.exists()) {
                rateLimiter.acquire();
                URL url = new URL(downloadURL);

                try (InputStream in = url.openStream()) {
                    Files.copy(in, Paths.get(imagePath));
                }
                return true;
            }
        }catch (IOException e) {
            throw new ClientException(e);
        }
        return false;
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
                        String maxId = retrieveLastKnownMaxId(poolId, voterInfo.getMax_id());
                        getNextStoryPollVoters(properties.getStoryPollVoterInfos(), media.getReelMedia().getId(), poolId, maxId);
                    }

                }
            }

            if (media.getReelExtraProperties().getStoryQuizs() != null) {
                for(StoryQuiz quiz : media.getReelExtraProperties().getStoryQuizs()) {
                    String quizId = String.valueOf(quiz.getQuizSticker().getQuizId());
                    List<StoryParticipantInfo> storyQuizParticipantInfos = properties.getStoryQuizParticipantInfos();
                    if (storyQuizParticipantInfos.size() > 0) {
                        StoryParticipantInfo participantInfo = storyQuizParticipantInfos.get(storyQuizParticipantInfos.size()-1);
                        String maxId = retrieveLastKnownMaxId(quizId, participantInfo.getMaxId());
                        getNextQuizParticipants(properties.getStoryQuizParticipantInfos(), media.getReelMedia().getId(), quizId, maxId);
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
        storeLastKnownMaxId(poolId, maxId);
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
        storeLastKnownMaxId(quizId, maxId);
        new MediaGetStoryQuizParticipantsRequest(reelId, quizId, maxId).execute(client).thenAccept(r->{
            participantInfos.add(r.getParticipant_info());
            getNextQuizParticipants(participantInfos, reelId, quizId, r.getNext_max_id());

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
        IGClient lib = new IGClient.Builder().username(username).password(password).login();
        lib.serialize(clientFile, cookieFile);
        return lib;
    }

    @SneakyThrows
    private IGClient getClientFromSerialize(File clientFile, File cookieFile) {
        return IGClient.deserialize(clientFile, cookieFile);
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

    private String retrieveLastKnownMaxId(String id, String defaultMaxId) {
        try {
            return FileUtils.readFileToString(new File(dataDir + "/" + id), forName("UTF-8"));
        } catch (IOException e) {
           return defaultMaxId;
        }
    }

    @SneakyThrows
    private void storeLastKnownMaxId(String id, String maxId) {
        FileUtils.writeStringToFile(new File(dataDir + "/" + id), maxId, forName("UTF-8"), false);
    }

    public static OkHttpClient formTestHttpClient(SerializableCookieJar jar) {
        return IGUtils.defaultHttpClientBuilder().cookieJar(jar).build();
    }
}
