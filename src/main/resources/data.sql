MERGE INTO genres (genre_id, name) VALUES (1, 'Комедия');
MERGE INTO genres (genre_id, name) VALUES (2, 'Драма');
MERGE INTO genres (genre_id, name) VALUES (3, 'Мультфильм');
MERGE INTO genres (genre_id, name) VALUES (4, 'Триллер');
MERGE INTO genres (genre_id, name) VALUES (5, 'Документальный');
MERGE INTO genres (genre_id, name) VALUES (6, 'Боевик');

MERGE INTO MPA (MPA_ID, name, description) VALUES (1, 'G', 'у фильма нет возрастных ограничений');
MERGE INTO MPA (MPA_ID, name, description) VALUES (2, 'PG', 'детям рекомендуется смотреть фильм с родителями');
MERGE INTO MPA (MPA_ID, name, description) VALUES (3, 'PG-13', 'детям до 13 лет просмотр не желателен');
MERGE INTO MPA (MPA_ID, name, description) VALUES (4, 'R', 'лицам до 17 лет просматривать фильм можно только в присутствии взрослого');
MERGE INTO MPA (MPA_ID, name, description) VALUES (5, 'NC-17', 'лицам до 18 лет просмотр запрещён');