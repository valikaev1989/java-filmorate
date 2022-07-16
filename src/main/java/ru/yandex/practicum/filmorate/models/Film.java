package ru.yandex.practicum.filmorate.models;

import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private HashSet<Integer> idLikeFilm = new HashSet<>();
    private HashSet<Genre> genres = new HashSet<>();
    private Mpa mpa;

    public Film(String name, String description, LocalDate releaseDate, Integer duration, Mpa mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public void addLike(Integer userId){
        this.idLikeFilm.add(userId);
    }
    public void addlikeId(Integer id){
        this.idLikeFilm.add(id);
    }
    public void addGenre(Genre genre){
        this.genres.add(genre);
    }

}