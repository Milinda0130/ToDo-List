package controller;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.ToDoList;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ToDoDashboardController implements Initializable {

    @FXML
    private ListView<CheckBox> listTodo;

    @FXML
    private DatePicker txtDate;

    @FXML
    private TextArea txtDescription;

    @FXML
    private TextField txtTitle;

    private ObservableList<ToDoList> taskList = FXCollections.observableArrayList();

    @FXML
    void btnAdd(ActionEvent event) {
        String SQL = "INSERT INTO todotable (title, description, date) VALUES (?, ?, ?)";

        // Input validation
        String title = txtTitle.getText();
        String description = txtDescription.getText();
        LocalDate date = txtDate.getValue();

        if (title.isEmpty() || description.isEmpty() || date == null) {
            new Alert(Alert.AlertType.ERROR,"input all fields").show();
            return;
        }

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, description);
            preparedStatement.setDate(3, Date.valueOf(date));
            preparedStatement.executeUpdate();
            showtasklist();
            clearFields();
        } catch (SQLException e) {

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showtasklist();
    }

    public void showtasklist() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            taskList.clear();

            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM todotable");

            listTodo.getItems().clear();

            while (resultSet.next()) {
                ToDoList todo = new ToDoList(
                        resultSet.getString("title"),
                        resultSet.getDate("date").toLocalDate(),
                        resultSet.getString("description")
                );


                CheckBox checkBox = new CheckBox(todo.getTitle() + " | " + todo.getDescription() + " (" + todo.getDate() + ")");
                listTodo.getItems().add(checkBox);
            }

        } catch (SQLException e) {
        }
    }

    private void clearFields() {
        txtTitle.clear();
        txtDescription.clear();
        txtDate.setValue(null);
    }


}
