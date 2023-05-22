package ru.yandex.practicum.filorate.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {

    private int id;
    private String email;
    private String login;
    public String name;
    private Date birthday;

    private Set<Integer> friends;
    private Set<Integer> filmsLikes;

    @JsonGetter("birthday")
    public String getCustomBirthday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(birthday);
    }

    public void addFriend(int id) {
        friends.add(id);
    }

    public void deleteFriend(int id) {
        friends.remove(id);
    }

    public void addLike(int id) {
        filmsLikes.add(id);
    }

    public void deleteLike(int id) {
        filmsLikes.remove(id);
    }

}
