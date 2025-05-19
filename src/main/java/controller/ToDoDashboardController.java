package controller;

import db.CompletedTaskDBConnection;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.ToDoList;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;

import java.util.ResourceBundle;

public class ToDoDashboardController implements Initializable {

    public TableView tbltoDo;
    public TableColumn colTitle;
    public TableColumn colDate;
    public TableColumn colDescription;

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

        String title = txtTitle.getText();
        String description = txtDescription.getText();
        LocalDate date = txtDate.getValue();

        if (title.isEmpty() || description.isEmpty() || date == null) {
            new Alert(Alert.AlertType.ERROR, "input all fields").show();
            return;
        }

        String SQL = "INSERT INTO todotable (title, description, date) VALUES (?, ?, ?)";

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
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        showtasklist();
        showCompletedTasks();
    }

    public void showtasklist() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            taskList.clear();
            listTodo.getItems().clear();

            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM todotable");

            while (resultSet.next()) {
                ToDoList todo = new ToDoList(
                        resultSet.getString("title"),
                        resultSet.getDate("date").toLocalDate(),
                        resultSet.getString("description")
                );

                CheckBox checkBox = new CheckBox(todo.getTitle() + " - " + todo.getDescription() + " (" + todo.getDate() + ")");
                listTodo.getItems().add(checkBox);

                checkBox.setOnAction(event -> {
                    if (checkBox.isSelected()) {

                        System.out.println("selected: " + todo.getTitle());


                        String SQL = "INSERT INTO completedtask (title, description, date) VALUES (?, ?, ?)";

                        try {
                            Connection connection1 = CompletedTaskDBConnection.getInstance().getConnection();
                            PreparedStatement preparedStatement = connection1.prepareStatement(SQL);
                            preparedStatement.setString(1,todo.getTitle());
                            preparedStatement.setString(2,todo.getDescription());
                            preparedStatement.setDate(3, Date.valueOf(todo.getDate()));
                            preparedStatement.executeUpdate();
                            showCompletedTasks();

                            String SQL1 = "DELETE FROM todotable WHERE title=?";
                            try {

                                Connection connection3 = DBConnection.getInstance().getConnection();
                                PreparedStatement preparedStatement1 = connection3.prepareStatement(SQL1);
                                preparedStatement1.setString(1,todo.getTitle());
                                preparedStatement1.executeUpdate();
                                showtasklist();




                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        tbltoDo.getItems().remove(todo);
                        System.out.println("deselected: " + todo.getTitle());
                    }
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        txtTitle.clear();
        txtDescription.clear();
        txtDate.setValue(null);
    }

    public void showCompletedTasks() {
        try {
            Connection connection = CompletedTaskDBConnection.getInstance().getConnection();
            ObservableList<ToDoList> completedTasks = FXCollections.observableArrayList();

            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM completedtask");

            while (resultSet.next()) {

                colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
                colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
                colDescription.setCellValueFactory(new PropertyValueFactory<>("Description"));

                tbltoDo.setItems(FXCollections.observableArrayList());
                ToDoList task = new ToDoList(
                        resultSet.getString("title"),
                        resultSet.getDate("date").toLocalDate(),
                        resultSet.getString("description")
                );
                completedTasks.add(task);
            }

            tbltoDo.setItems(completedTasks);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
