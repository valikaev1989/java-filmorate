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
SELECT *, f.name as movie,
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
FROM friends_user."friend_id" AS f1
JOIN friends_user."friend_id" AS f2 ON f.friend_id = f2.friend_id
JOIN user AS u ON f.friend_id = u.user_id
GROUP BY u.name;
```
5. выдать статус подтверждения дружбы
``` java
SELECT u.name,
f.friens_user,
s.name
FROM user as u
LEFT JOIN friends_user AS fu ON u.user_id = fu.user_id
LEFT JOIN friends_status as fs ON fu.friend_id = fs.friend_id
LEFT JOIN status as s ON fs.status_id = s.status_id
GROUP BY u.name;
```
![связи между данными](C:\Users\valik\Desktop\dev\java-filmorate\QuickDBD-filmogram Diagram.svg "Диаграмма")
