---

# Это репозиторий проекта *"filmorate"*

Приложение умеет:

1. создавать записи о фильмах и свой аккаунт
2. ставить *"like"* понравившемуся фильму
3. заводить друзей

## *Диаграмма базы данных*

Примеры запросов:

1. выдать всех пользователей
``` java
SELECT *
FROM users; 
```
2. выдать все фильмы
``` java
SELECT *
FROM films; 
```
3. получение *"N"* популярных фильмов
``` java
SELECT *, f.name as movie, //* остальные поля из таблицы films
r.film_rating_id as film_rating
FROM films as f
LEFT JOIN genres AS g on f.genre_id = g.genre_id
LEFT JOIN ratings as r on f.film_rating = r.rating_id
LEFT JOIN films_likes as fl on f.film_id = fl.film_id
ORDER BY film_rating DESC
LIMIT "количество фильмов в списке";
```
4. выдать общих друзей
``` java
SELECT u.name
FROM users_friends."friend_id" AS f1
JOIN users_friends."friend_id" AS f2 ON f.friend_id = f2.friend_id
JOIN user AS u ON f.friend_id = u.user_id
GROUP BY u.name;
```

<img src="C:\Users\valik\Desktop\dev\java-filmorate\src\main\resources\QuickDBD-filmogram Diagram.png"/>