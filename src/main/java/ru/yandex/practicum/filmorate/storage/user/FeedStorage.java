package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedStorage {
    void addInFeed(long userId, String eventType, String operation, long entityId);
    List<Feed> getFeedByUserId(long userId);
}