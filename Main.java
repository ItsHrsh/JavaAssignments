package com.example.assignment1;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class Main extends Application {

    private final String url = "jdbc:mysql://localhost:3306/imdb_movie_data";
    private final String username = "root";
    private final String password = "Harsh@2003";
    @Override
    public void start(Stage primaryStage) {
        // Creating the button
        Button showDataButton = new Button("Show Data");
        showDataButton.setOnAction(e -> showDataInTableView());
        showDataButton.setStyle("-fx-background-color: #e86423; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-family: 'Arial';");


        // Creating the BarChart
        BarChart<String, Number> barChart = createBarChart();

        // Adding logo image
        Image iconImage = new Image("file:///C:/Users/hrsh0/IdeaProjects/Assignment1/bar-chart-fill.png");

        // Creating a VBox to hold the button and the chart
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(barChart); // Removed the button from the VBox
        vbox.setStyle("-fx-background-color: #9c9b9b; -fx-font-family: 'Arial';");

        // Creating an HBox to hold the button at the bottom right corner
        HBox buttonBox = new HBox(showDataButton);
        buttonBox.setSpacing(10);
        buttonBox.setStyle("-fx-padding: 10px; -fx-alignment: center-right;"); // Align the button to the right
        vbox.getChildren().add(buttonBox);



        // Creating a BorderPane for the root layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f0f0f0;");

        // Creating a custom title bar with logo


        // Adding vbox to the bottom right corner
        root.setCenter(vbox); // Changed from setBottom to setCenter

        Scene scene = new Scene(root, 800, 600);

        // Setting the scene to the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Most voted movies bar graph");
        root.setStyle("-fx-font-color: #Fffefe;");
        primaryStage.getIcons().add(iconImage); // Set the icon for the title bar
        primaryStage.show();
    }

    private BarChart<String, Number> createBarChart() {
        // Creating a bar chart
        BarChart<String, Number> barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        barChart.setTitle("Top 5 movies with most votes");

        barChart.getXAxis().setLabel("Movie Title");
        barChart.getYAxis().setLabel("Number of Votes");

        // Fetching data and adding it to the chart
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM movies");

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Votes");

            int count = 0;
            while (resultSet.next() && count < 5) { // Display only top 5 movies
                String title = resultSet.getString("title");
                double rating = resultSet.getDouble("num_of_votes");
                series.getData().add(new XYChart.Data<>(title, rating));
                count++;
            }

            barChart.getData().add(series);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return barChart;
    }

    private void showDataInTableView() {
        TableView<ObservableList<String>> tableView = new TableView<>();
        tableView.setStyle("-fx-background-color: #d3d3d3;");

        // Fetching column names from the ResultSet metadata
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM movies");

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Creating TableColumn instances for each column
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(metaData.getColumnName(columnIndex));
                final int columnIndexFinal = columnIndex; // for lambda expression
                column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(columnIndexFinal - 1)));
                tableView.getColumns().add(column);
            }

            // Adding rows to the TableView
            while (resultSet.next()) {
                ObservableList<String> rowData = FXCollections.observableArrayList();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    rowData.add(resultSet.getString(columnIndex));
                }
                tableView.getItems().add(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Creating a new stage for the TableView
        Stage stage = new Stage();
        stage.setScene(new Scene(tableView, 400, 400));
        stage.setTitle("Data Table");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
