CREATE TABLE IF NOT EXISTS users (
	user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name VARCHAR(100),
	email VARCHAR(100) NOT NULL,
	login VARCHAR(100) NOT NULL,
	birthday DATE NOT NULL,
	CONSTRAINT not_future CHECK (birthday <= CURRENT_DATE())
);

CREATE TABLE IF NOT EXISTS friends (
	user_id BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
	friend_id BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
	is_confirmed BOOLEAN DEFAULT FALSE,
	CONSTRAINT friends_pair UNIQUE (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS MPA (
    MPA_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(12) NOT NULL,
	description VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS films (
    film_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(200) NOT NULL,
    duration BIGINT NOT NULL,
    MPA_id INTEGER NOT NULL REFERENCES MPA (MPA_id),
    release_date DATE NOT NULL,
    CONSTRAINT chk_films_duration CHECK(duration>0)
);

CREATE TABLE IF NOT EXISTS likes (
    film_id BIGINT NOT NULL REFERENCES films (film_id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT film_user UNIQUE (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id BIGINT NOT NULL REFERENCES films (film_id) ON DELETE CASCADE,
    genre_id INTEGER NOT NULL REFERENCES genres (genre_id),
    CONSTRAINT film_genre UNIQUE (film_id, genre_id)
);