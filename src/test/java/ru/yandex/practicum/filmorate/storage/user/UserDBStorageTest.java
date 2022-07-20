package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.dao.UserDbStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDBStorageTest {
    private final UserDbStorage userStorage;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Test
    @Order(1)
    void addUser() {
        User user = new User();
        user.setName("Mikhail");
        user.setEmail("test@gmail.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.parse("13.04.1986", formatter));
        userStorage.addUser(user);
        Optional<User> userOptional = userStorage.getUserById(1);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Mikhail")
                                .hasFieldOrPropertyWithValue("login", "login")
                                .hasFieldOrPropertyWithValue("email", "test@gmail.com")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.parse("13.04.1986", formatter))
                );
    }

    @Test
    @Order(2)
    void updateUser() {
        User user = userStorage.getUserById(1).orElse(new User());
        user.setLogin("Mikhail");
        userStorage.updateUser(user);

        Optional<User> userOptional = userStorage.getUserById(1);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Mikhail")
                                .hasFieldOrPropertyWithValue("login", "Mikhail")
                                .hasFieldOrPropertyWithValue("email", "test@gmail.com")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.parse("13.04.1986", formatter))
                );
    }

    @Test
    @Order(3)
    void findAll() {
        User user = new User();
        user.setName("Alex");
        user.setEmail("alex@gmail.com");
        user.setLogin("Al");
        user.setBirthday(LocalDate.parse("11.01.1981", formatter));
        userStorage.addUser(user);

        List<User> users = userStorage.findAll();

        Optional<User> userOptional = Optional.of(users.get(0));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).
                                hasFieldOrPropertyWithValue("id", 1L).
                                hasFieldOrPropertyWithValue("name", "Mikhail").
                                hasFieldOrPropertyWithValue("login", "Mikhail").
                                hasFieldOrPropertyWithValue("email", "test@gmail.com").
                                hasFieldOrPropertyWithValue("birthday", LocalDate.parse("13.04.1986", formatter))
                );

        userOptional = Optional.of(users.get(1));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u)
                                .hasFieldOrPropertyWithValue("id", 2L)
                                .hasFieldOrPropertyWithValue("name", "Alex")
                                .hasFieldOrPropertyWithValue("login", "Al")
                                .hasFieldOrPropertyWithValue("email", "alex@gmail.com")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.parse("11.01.1981", formatter))
                );
    }

    @Test
    @Order(4)
    void getUserById() {
        Optional<User> userOptional = userStorage.getUserById(1);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Mikhail")
                                .hasFieldOrPropertyWithValue("login", "Mikhail")
                                .hasFieldOrPropertyWithValue("email", "test@gmail.com")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.parse("13.04.1986", formatter))
                );
    }

    @Test
    @Order(5)
    void deleteUserById() {
        userStorage.deleteUserById(1);
        userStorage.deleteUserById(2);
        List<User> users = userStorage.findAll();
        assertThat(users.isEmpty()).isTrue();
    }

    @Test
    @Order(6)
    void addFriend() {
        User user = new User();
        user.setName("Mikhail");
        user.setEmail("test@gmail.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.parse("13.04.1986", formatter));
        userStorage.addUser(user);
        long firstUserId = user.getId();

        user.setName("Alex");
        user.setEmail("alex@gmail.com");
        user.setLogin("Al");
        user.setBirthday(LocalDate.parse("11.01.1981", formatter));
        userStorage.addUser(user);

        userStorage.addFriend(firstUserId, user.getId());

        Optional<User> userOptional = userStorage.getUserById(firstUserId);
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("friends", Set.copyOf(List.of(user.getId())))
                );

    }

    @Test
    @Order(7)
    public void confirmFriend() {
        assertThat(userStorage.isConfirmFriend(3L, 4L))
                .isPresent().hasValueSatisfying(i -> assertThat(i).isFalse());
        userStorage.confirmFriend(3L, 4L);
        assertThat(userStorage.isConfirmFriend(3L, 4L))
                .isPresent().hasValueSatisfying(i -> assertThat(i).isTrue());
    }

    @Test
    @Order(8)
    void deleteFriend() {
        List<User> users = userStorage.findAll();
        userStorage.deleteFriend(users.get(0).getId(), users.get(1).getId());

        Optional<User> userOptional = userStorage.getUserById(users.get(0).getId());
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("friends", new HashSet<>())
                );
    }
}