package model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class ToDoList {

    private String topic;
    private String Description;
    private LocalDate date;
    private LocalTime time;

}
