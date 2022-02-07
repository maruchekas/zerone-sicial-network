package com.skillbox.javapro21.service.impl;

import com.skillbox.javapro21.api.response.statistics.*;
import com.skillbox.javapro21.domain.Person;
import com.skillbox.javapro21.domain.Post;
import com.skillbox.javapro21.domain.PostComment;
import com.skillbox.javapro21.domain.PostLike;
import com.skillbox.javapro21.repository.PersonRepository;
import com.skillbox.javapro21.repository.PostCommentRepository;
import com.skillbox.javapro21.repository.PostLikeRepository;
import com.skillbox.javapro21.repository.PostRepository;
import com.skillbox.javapro21.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final PersonRepository personRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;

    @Override
    public StatisticsResponse getAllStatistic() {
        Long countPersons = personRepository.findCountPerson();
        Long countLikes = postLikeRepository.count();
        Long countPosts = postRepository.findCountPosts();
        Long countComments = postCommentRepository.findCountComments();
        return new StatisticsResponse()
                .setUsersCount(countPersons)
                .setPostsCount(countPosts)
                .setLikesCount(countLikes)
                .setCommentsCount(countComments);
    }

    @Override
    public PostStatResponse getPostsStatistic() {
        Long countPosts = postRepository.findCountPosts();
        List<Post> postList = postRepository.allPosts();
        Map<YearMonth, Long> posts = getMapForAllStat(postList);
        Map<Integer, Long> postsByHour = getMapForHourStat(postList);
        return new PostStatResponse()
                .setCountPosts(countPosts)
                .setPosts(posts)
                .setPostsByHour(postsByHour);
    }

    @Override
    public UsersStatResponse getUsersStatistic() {
        Long countPersons = personRepository.findCountPerson();
        List<Person> personList = personRepository.findAllPersons();
        Map<LocalDate, Long> dynamic = new TreeMap<>();
        List<Person> personListWithBD = personRepository.findAllPersonsWithBirthday();
        YearsUsersStat yearsUsersStat = new YearsUsersStat()
                .setYoung(getPercentPersonsByYearsOld(personListWithBD,0, 18))
                .setTeenager(getPercentPersonsByYearsOld(personListWithBD,18, 25))
                .setAdult(getPercentPersonsByYearsOld(personListWithBD,25, 45))
                .setElderly(getPercentPersonsByYearsOld(personListWithBD,45, 1000));
        for (int i = 0; i < 10; i++) {
            LocalDateTime localDateTime = LocalDateTime.now().minusDays(10 - i);
            LocalDate localDate = localDateTime.toLocalDate();
            long count = personList.stream()
                    .filter(p -> p.getRegDate().getDayOfMonth() == (localDateTime.getDayOfMonth()))
                    .count();
            dynamic.put(localDate, count);
        }
        return new UsersStatResponse()
                .setUsersCount(countPersons)
                .setDynamic(dynamic)
                .setYearsUsersStat(yearsUsersStat);
    }

    @Override
    public CommentsStatResponse getCommentsStatistic() {
        Long countComments = postCommentRepository.findCountComments();
        List<PostComment> commentsList = postCommentRepository.allComments();
        Map<YearMonth, Long> comments = new TreeMap<>();
        Map<Integer, Long> commentsByHour = new TreeMap<>();
        for (int i = 0; i < 13; i++) {
            LocalDateTime localDateTime = LocalDateTime.now().minusMonths(12 - i);
            YearMonth month = YearMonth.of(localDateTime.getYear(), localDateTime.getMonth());
            long count = commentsList.stream()
                    .filter(c -> c.getTime().getMonth().equals(localDateTime.getMonth()))
                    .count();
            comments.put(month, count);
        }
        for (int i = 0; i < 25; i++) {
            LocalDateTime localDateTime = LocalDateTime.now().minusHours(24 - i);
            Integer time = localDateTime.toLocalTime().getHour();
            long count = commentsList.stream()
                    .filter(c -> c.getTime().getDayOfMonth() == LocalDateTime.now().getDayOfMonth())
                    .filter(c -> c.getTime().getHour() == (localDateTime.getHour()))
                    .count();
            commentsByHour.put(time, count);
        }
        return new CommentsStatResponse()
                .setCommentsCount(countComments)
                .setComments(comments)
                .setCommentsByHour(commentsByHour);
    }

    @Override
    public LikesStatResponse getLikesStatistic() {
        Long countLikes = postLikeRepository.findCountLikes();
        List<PostLike> likesList = postLikeRepository.findAll();
        Map<YearMonth, Long> likes = new TreeMap<>();
        Map<Integer, Long> likesByHour = new TreeMap<>();
        for (int i = 0; i < 13; i++) {
            LocalDateTime localDateTime = LocalDateTime.now().minusMonths(12 - i);
            YearMonth month = YearMonth.of(localDateTime.getYear(), localDateTime.getMonth());
            long count = likesList.stream()
                    .filter(c -> c.getTime().getMonth().equals(localDateTime.getMonth()))
                    .count();
            likes.put(month, count);
        }
        for (int i = 0; i < 25; i++) {
            LocalDateTime localDateTime = LocalDateTime.now().minusHours(24 - i);
            Integer time = localDateTime.toLocalTime().getHour();
            long count = likesList.stream()
                    .filter(c -> c.getTime().getDayOfMonth() == LocalDateTime.now().getDayOfMonth())
                    .filter(c -> c.getTime().getHour() == (localDateTime.getHour()))
                    .count();
            likesByHour.put(time, count);
        }
        return new LikesStatResponse()
                .setLikesCount(countLikes)
                .setLikes(likes)
                .setLikesByHour(likesByHour);
    }

    private Map<Integer, Long> getMapForHourStat(List<Post> list) {
        Map<Integer, Long> allHourStat = new TreeMap<>();
        for (int i = 0; i < 25; i++) {
            LocalDateTime localDateTime = LocalDateTime.now().minusHours(24 - i);
            Integer time = localDateTime.toLocalTime().getHour();
            long count = list.stream()
                    .filter(p -> p.getTime().getDayOfMonth() == LocalDateTime.now().getDayOfMonth())
                    .filter(p -> p.getTime().getHour() == (localDateTime.getHour()))
                    .count();
            allHourStat.put(time, count);
        }
        return allHourStat;
    }

    private Map<YearMonth, Long> getMapForAllStat(List<Post> list) {
        Map<YearMonth, Long> allStat = new TreeMap<>();
        for (int i = 0; i < 13; i++) {
            LocalDateTime localDateTime = LocalDateTime.now().minusMonths(12 - i);
            YearMonth month = YearMonth.of(localDateTime.getYear(), localDateTime.getMonth());
            long count = list.stream()
                    .filter(a -> a.getTime().getMonth().equals(localDateTime.getMonth()))
                    .count();
            allStat.put(month, count);
        }
        return allStat;
    }

    private String getPercentPersonsByYearsOld(List<Person> personList, int from, int before) {
        long count = personList.size();
        LocalDateTime fromTime = LocalDateTime.now().minusYears(from);
        LocalDateTime beforeTime = LocalDateTime.now().minusYears(before);
        int size = personRepository.findAllPersonsByYearsOld(beforeTime, fromTime).size();
        double l = (double) size / count * 100.0;
        return l + " %";
    }
}
