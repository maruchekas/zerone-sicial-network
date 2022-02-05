package com.skillbox.javapro21.service;

import com.skillbox.javapro21.api.response.statistics.*;
import org.springframework.stereotype.Service;

@Service
public interface StatisticsService {
    StatisticsResponse getAllStatistic();

    PostStatResponse getPostsStatistic();

    UsersStatResponse getUsersStatistic();

    CommentsStatResponse getCommentsStatistic();

    LikesStatResponse getLikesStatistic();
}
