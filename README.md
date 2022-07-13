# java-filmorate
## Description
 This is a service for determining movie ratings based on user likes.

The following commands are available in this service:
1. To manage users:
- Create a user;
- Update user;
- Add user to friends;
- Remove user from friends;
- Get a list of all users;
- Get the user by his Id;
- Get a list of user's friends;
- Get a list of mutual friends of users of two users;
2. To manage films:
- Add film;
- Update film;
- Add a like to the film;
- Remove like film;
- Get a list of all films;
- Get a film by its Id;
- Get top N films sorted in descending order of rating;
- Get a list of all film genres;
- Get a list of all film age ratings.

## Database

![ER-diagram DB](schema.png)

## Description of tables

#### *MPA*
This table is intended for storing a list of age ratings that can be assigned to film.

- **MPA_id** - rating ID;
- **name** - rating name;
- **description** - rating description.

#### *genres*
This table keeps a list of available movie genres:

- **genre_id** - genre ID;
- **name** - genre name.

#### *films*
This table keeps a list of film:

- **film_id** - film ID;
- **name** - film name;
- **description** - film description;
- **duration** - film duration;
- **MPA_id** - rating ID;
- **release_date** - release date film.

#### *film_genre*
This table keeps a list of film and genre matches:

- **film_id** - film ID;
- **genre_id** - genre ID.

#### *likes*
This table contains information about who liked what movie:

- **film_id** - film ID.
- **user_id** - user ID.

#### *users*
This table contains information about users:

- **user_id** - user ID;
- **email** - user email;
- **login** - user login;
- **name** - user name;
- **birthday** - user's birthday.

#### Таблица *friends*
This table contains information about users' friends:

- **user_id** - user ID;
- **friend_id** - friend ID;
- **is_confirm** - friendship status.

### Main SQL queries for films:

    SQL_GET_FILM_BY_ID = SELECT 
            f.film_id AS f_id,
            f.name AS f_name,
            f.description AS f_description,
            f.duration AS f_duration,
            m.MPA_id AS m_id,
            m.name AS m_name,
            m.description AS m_description,
            f.release_date AS f_release_date
            FROM (SELECT * FROM films AS f WHERE film_id=?) AS f
            LEFT JOIN MPA AS m ON f.MPA_id=m.MPA_id;

    SQL_GET_ALL_FILMS = SELECT 
            f.film_id AS f_id,
            f.name AS f_name,
            f.description AS f_description,
            f.duration AS f_duration,
            m.MPA_id AS m_id,
            m.name AS m_name,
            m.description AS m_description,
            f.release_date AS f_release_date
            FROM films AS f
            LEFT JOIN MPA AS m ON f.MPA_id=m.MPA_id;

    SQL_ADD_FILM = INSERT INTO films (
            name,
            description,
            release_date,
            duration,
            MPA_id) 
            VALUES (?, ?, ?, ?, ?);
    SQL_UPDATE_FILM = UPDATE films SET
            name = ?,
            description = ?,
            release_date = ?,
            duration = ?,
            MPA_id = ?
            WHERE film_id = ?;
    SQL_DELETE_FILM = DELETE FROM films WHERE film_id = ?;
    SQL_ADD_LIKE = INSERT INTO likes (film_id, user_id) VALUES (?, ?);
    SQL_DELETE_LIKE = DELETE FROM likes WHERE film_id = ? AND user_id = ?;
    SQL_GET_LIKES_BY_FILM_ID = SELECT user_id FROM likes WHERE film_id = ?;
    SQL_GET_GENRE_BY_FILM_ID = SELECT genre_id FROM FILM_GENRE WHERE film_id = ? ORDER BY genre_id;
    SQL_ADD_GENRE = INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?);
    SQL_DELETE_GENRE = DELETE FROM film_genre WHERE film_id = ?;
    SQL_GET_ALL_MPA = SELECT * FROM MPA ORDER BY MPA_id;
    SQL_GET_ALL_GENRE = SELECT * FROM genres ORDER BY genre_id;
    SQL_GET_POPULAR_FILMS = SELECT
            f.film_id AS f_id,
            f.name AS f_name,
            f.description AS f_description,
            f.duration AS f_duration,
            m.MPA_id AS m_id,
            m.name AS m_name,
            m.description AS m_description,
            f.release_date AS f_release_date
            FROM films AS f
            LEFT JOIN
            (SELECT film_id, COUNT(user_id) as cnt FROM likes
            GROUP BY film_id ) AS l ON f.film_id = l.film_id
            LEFT JOIN MPA AS m ON f.MPA_id=m.MPA_id
            ORDER BY cnt DESC LIMIT ?;

### Main SQL queries for users:
    SQL_GET_USERS = SELECT * FROM users ORDER BY user_id;
    SQL_GET_USER_BY_ID = SELECT * FROM users WHERE user_id = ?;
    SQL_ADD_USER = INSERT INTO users (name, email, login, birthday)
    VALUES (?, ?, ?, ?);
    SQL_UPDATE_USER =  UPDATE users SET name = ?, email = ?, login = ?,
    birthday = ?
    WHERE user_id = ?;
    SQL_ADD_FRIEND = INSERT INTO friends (user_id, friend_id) VALUES (?, ?);
    SQL_CONFIRM_FRIEND = UPDATE friends SET is_confirmed = ?
    WHERE user_id = ? AND friend_id = ?;
    SQL_DELETE_USER_BY_ID = DELETE FROM users WHERE user_id = ?;
    SQL_DELETE_FRIEND = DELETE FROM friends WHERE user_id = ? AND friend_id = ?;
    SQL_GET_FRIEND_BY_ID = SELECT friend_id FROM friends WHERE user_id = ?;
    SQL_GET_STATUS_FRIEND = SELECT is_confirmed FROM friends WHERE user_id = ?
    AND FRIEND_ID = ?;
    SQL_GET_LAST_ID = SELECT * FROM users ORDER BY user_id DESC LIMIT 1;