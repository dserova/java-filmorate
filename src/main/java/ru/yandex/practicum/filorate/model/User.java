package ru.yandex.practicum.filorate.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@AllArgsConstructor
public class User {

    private int id;
    private String email;
    private String login;
    public String name;
    private Date birthday;

    @JsonGetter("birthday")
    public String getCustomBirthday() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(birthday);
    }

}
