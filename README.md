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
FROM user; 
```
2. выдать все фильмы
``` java
SELECT *
FROM film; 
```
3. получение *"N"* популярных фильмов
``` java
SELECT *, f.name as movie, //* остальные поля из таблицы film
r.rating_id as film_rating
FROM film as f
LEFT JOIN genre AS g on f.genre_id = g.genre_id
LEFT JOIN rating as r on f.rating_id = r.rating_id
LEFT JOIN film_liked as fl on f.film_id = fl.film_id
ORDER BY film_rating DESC
LIMIT "количество фильмов в списке";
```
4. выдать общих друзей
``` java
SELECT u.name
FROM friendship."friend_id" AS f1
JOIN friendship."friend_id" AS f2 ON f.friend_id = f2.friend_id
JOIN user AS u ON f.friend_id = u.user_id
GROUP BY u.name;
```

<img src="C:\Users\valik\Desktop\dev\java-filmorate\src\main\resources\QuickDBD-filmogram Diagram.png"/>