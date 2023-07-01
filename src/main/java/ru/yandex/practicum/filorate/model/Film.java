package ru.yandex.practicum.filorate.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class Film {

    private int id;
    private String name;
    private String description;
    private Date releaseDate;
    private int duration;
    private Integer rate;

    private List<Genre> genres;

    private AgeRatingSystem mpa;

    @JsonGetter("releaseDate")
    public String getCustomData() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(releaseDate);
    }

    public void addLike() {
        rate++;
    }

    public void deleteLike() {
        rate--;
    }

}
