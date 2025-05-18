package model;

import com.mysql.cj.conf.BooleanProperty;
import javafx.scene.control.CheckBox;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class ToDoList {


    private String title;
    private LocalDate date;
    private String Description;
    private CheckBox checkBox;

    public ToDoList(String title, LocalDate date,String description) {
        this.title = title;
        this.date = date;
        Description = description;
    }
}
