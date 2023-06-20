-- Delete table section(reuse) -------------------------------------------------

-- DELETE FROM public.USER_FILMS_LIKES;
-- DELETE FROM public.FILM_GENRES;
-- DELETE FROM public.USER_FRIENDS;
--
-- DELETE FROM public.FILM;
-- ALTER TABLE public.FILM ALTER COLUMN film_id RESTART WITH 1;
--
-- DELETE FROM public.GENRE ;
--- ALTER TABLE public.GENRE ALTER COLUMN genre_id RESTART WITH 1;
--
-- DELETE FROM public.USER;
-- ALTER TABLE public.USER ALTER COLUMN user_id RESTART WITH 1;
--
-- DELETE FROM public.AGE_RATING_SYSTEM;
-- ALTER TABLE public.AGE_RATING_SYSTEM ALTER COLUMN ars_id RESTART WITH 1;

-- Insert Data section -------------------------------------------------

MERGE INTO public.AGE_RATING_SYSTEM (ars_id, name)
VALUES
  (1, 'G'),
  (2, 'PG'),
  (3, 'PG-13'),
  (4, 'R'),
  (5, 'NC-17');

MERGE INTO public.GENRE (genre_id, name)
VALUES
  (1, 'Комедия'),
  (2, 'Драма'),
  (3, 'Мультфильм'),
  (4, 'Триллер'),
  (5, 'Документальный'),
  (6, 'Боевик');
