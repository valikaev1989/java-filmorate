package ru.yandex.practicum.filmorate.models.User;

import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.HashMap;

class FriendStatus {
    private static final HashMap<Integer, String> friendshipStatus = new HashMap<>();

    public FriendStatus() {
        friendshipStatus.put(1, "not confirmed");
        friendshipStatus.put(2, "confirmed");
    }

    String getStatus(int statusId) {
        if (friendshipStatus.containsKey(statusId)) {
            return friendshipStatus.get(statusId);
        } else {
            throw new ValidationException("некорректный id статуса дружбы");
        }
    }
}
