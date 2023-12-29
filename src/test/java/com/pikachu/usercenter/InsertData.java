package com.pikachu.usercenter;

import com.pikachu.usercenter.model.entity.User;
import com.pikachu.usercenter.service.UserService;
import jakarta.annotation.Resource;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.*;

import static com.pikachu.usercenter.constant.UserConstant.SALT;
/**
 * @author 笨蛋皮卡丘
 * @version 1.0
 */
@SpringBootTest
public class InsertData {

    @Resource
    private UserService userService;

    private final ExecutorService executorService = new ThreadPoolExecutor(20, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    @Test
    void insertUsers() throws IOException {
        File tagsFile = ResourceUtils.getFile("classpath:tags.txt");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(tagsFile));
        String tag;
        List<String> tags = new ArrayList<>();
        while ((tag = bufferedReader.readLine()) != null) {
            tags.add(tag);
        }

        Faker faker1 = new Faker(new Locale("en"));
        Faker faker2 = new Faker(new Locale("zh-CN"));
        List<Faker> fakers = Arrays.asList(faker1, faker2);


        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int batchSize = 5000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            List<User> userList = new ArrayList<>();
            do {
                j++;
                User user = new User();
                Faker randomFaker = new Faker().options().nextElement(fakers);
                user.setAccount(randomFaker.regexify("\\w[a-zA-Z0-9-]{3,15}"));
                user.setNickname(randomFaker.name().name());
                user.setAvatarUrl(randomFaker.avatar().image());
                user.setProfile(randomFaker.text().text(0, 256));
                user.setGender(randomFaker.bool().bool());
                user.setAge(randomFaker.random().nextInt(1, 199));
                user.setPassword(DigestUtils.md5DigestAsHex((SALT + randomFaker.internet().password(8, 20, true, true, true)).getBytes()));
                user.setPhone(randomFaker.phoneNumber().cellPhone());
                user.setEmail(faker1.internet().emailAddress());
                user.setStatus(0);
                Date create_time = randomFaker.date().between(new Date(1262304000000L), new Date());
                user.setCreateTime(create_time.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                user.setUpdateTime(randomFaker.date().between(create_time, new Date()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                user.setIsDelete(false);
                user.setRole(0);
                user.setTags(getRandomElements(tags));

                user.setAccount(user.getAccount() + "_" + j);
                userList.add(user);
            } while (j % batchSize != 0);

            // 异步执行
            int finalJ = j;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println(finalJ);
                userService.saveBatch(userList, batchSize);
            }, executorService);
            futureList.add(future);
        }

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    public static <T> List<T> getRandomElements(List<T> originalList) {
        Set<T> resultSet = new HashSet<>();
        Random random = new Random();
        int numberOfElementsToPick = random.nextInt(originalList.size()) + 1;
        while (resultSet.size() < numberOfElementsToPick) {
            resultSet.add(originalList.get(random.nextInt(originalList.size())));
        }
        return resultSet.stream().toList();
    }
}
