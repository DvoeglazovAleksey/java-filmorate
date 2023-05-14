package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO Users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update("UPDATE users SET email=?, login=?, name=?, birthday=? WHERE user_id=?",
                user.getEmail(), user.getLogin(), user.getName(), Date.valueOf(user.getBirthday()), user.getId());
        return user;
    }

    @Override
    public List<User> findUsers() {
        return jdbcTemplate.query("SELECT * FROM Users", (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User findUserById(long id) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public void addFriend(long userId, long friendId) {
        try {
            String sql = "INSERT INTO Friendship (user_id, friend_id, status) VALUES(?, ?, false)";
            jdbcTemplate.update(sql, userId, friendId);
        } catch (DataAccessException exception) {
            log.error("Пользователь с id = {} уже в друзьях у пользователя с id = {}", friendId, userId);
            throw new ValidationException(String.format("Пользователь с id = %s уже в друзьях у пользователя с id = %s",
                    friendId, userId));
        }
    }

    @Override
    public void updateFriendship(long userId, long friendId, boolean status) {
        String sql = "UPDATE Friendship SET status = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, status, userId, friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        String sql = "DELETE FROM Friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> findAllFriendsById(long userId) {
        String sql = "SELECT * FROM Users WHERE user_id IN (SELECT friend_id FROM Friendship WHERE user_id = ?)";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId);
    }

    @Override
    public List<User> findCommonFriends(long userId, long friendId) {
        List<User> commonFriends = new ArrayList<>();
        List<User> friendsOfUser = findAllFriendsById(userId);
        List<User> friendsOfFriend = findAllFriendsById(friendId);
        for (User user : friendsOfUser) {
            if (friendsOfFriend.contains(user)) {
                commonFriends.add(user);
            }
        }
        return commonFriends;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getInt("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate()).build();
    }
}
