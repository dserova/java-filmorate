create table if not exists public.age_rating_system (ars_id integer generated by default as identity, name varchar(255), primary key (ars_id));
create table if not exists public.film (film_id integer generated by default as identity, description varchar(255), duration integer not null, name varchar(255), rate integer, release_date timestamp, mpa_ars_id integer, primary key (film_id));
create table if not exists public.genre (genre_id integer generated by default as identity, name varchar(255), primary key (genre_id));
create table if not exists public.user (user_id integer generated by default as identity, birthday timestamp, email varchar(255), login varchar(255), name varchar(255), primary key (user_id));
create table if not exists film_genres (film_film_id integer not null, genres_genre_id integer not null, primary key (film_film_id, genres_genre_id));
create table if not exists user_films_likes (user_user_id integer not null, films_likes_film_id integer not null, primary key (user_user_id, films_likes_film_id));
create table if not exists user_friends (user_user_id integer not null, friends_user_id integer not null, primary key (user_user_id, friends_user_id));
alter table public.film add constraint if not exists FK001 foreign key (mpa_ars_id) references public.age_rating_system;
alter table film_genres add constraint if not exists FK002 foreign key (genres_genre_id) references public.genre;
alter table film_genres add constraint if not exists FK003 foreign key (film_film_id) references public.film;
alter table user_films_likes add constraint if not exists FK004 foreign key (films_likes_film_id) references public.film;
alter table user_films_likes add constraint if not exists FK005 foreign key (user_user_id) references public.user;
alter table user_friends add constraint if not exists FK006 foreign key (friends_user_id) references public.user;
alter table user_friends add constraint if not exists FK007 foreign key (user_user_id) references public.user;
INSERT INTO public.GENRE (genre_id, name) VALUES (1,'Комедия'), (2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик');
INSERT INTO public.age_rating_system (ars_id, name) VALUES (1,'G'), (2, 'PG'), (3, 'PG-13'), (4, 'R'), (5, 'NC-17');
