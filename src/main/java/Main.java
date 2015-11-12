import Summoners.Games;
import Summoners.Summoner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Calls Riot's API and displays a Line chart for the Kills, Deaths, and Assists for the last 10 Games played
 */
public class Main extends Application {


    public static void main(String[] args) {
        launch(args);

    }


    @Override
    @SuppressWarnings("unchecked")
    public void start(Stage primaryStage) throws Exception {

        String games = ApiCall.getRiotJSON("https://na.api.pvp.net/api/lol/na/v1.3/game/by-summoner/20806859/recent");

        Gson gamesGson = new GsonBuilder().create();

        Summoner gamesData = gamesGson.fromJson(games, Summoner.class);

        //Set up Axis for Line Chart
        final NumberAxis xAxis = new NumberAxis(0, 10, 1);
        final NumberAxis yAxis = new NumberAxis(0, 40, 1);


        //Creating the Chart
        final LineChart<Number, Number> killsLineChart = new LineChart<>(xAxis, yAxis);

        //Making a kills, deaths, and assists Series
        XYChart.Series killsSeries = new XYChart.Series();
        killsSeries.setName("Kills per Game");

        XYChart.Series deathsSeries = new XYChart.Series();
        deathsSeries.setName("Deaths per game");

        XYChart.Series assistSeries = new XYChart.Series();
        assistSeries.setName("Assists per game");


        //Make Games Array List
        ArrayList<Games> killData = new ArrayList<>();
        killData.addAll(Arrays.asList(gamesData.games));

        //Set up Data for Pie Chart
        int kills = 0;
        int deaths = 0;
        int assists = 0;

        //for loop to add data to each Series and to get total kills, deaths, and assists
        for (int i = 0; i < killData.size(); i++) {
            killsSeries.getData().add(new XYChart.Data<>(i, killData.get(i).stats.championsKilled));
            kills += killData.get(i).stats.championsKilled;

            deathsSeries.getData().add(new XYChart.Data<>(i, killData.get(i).stats.numDeaths));
            deaths += killData.get(i).stats.numDeaths;

            assistSeries.getData().add(new XYChart.Data<>(i, killData.get(i).stats.assists));
            assists += killData.get(i).stats.assists;
        }



        //Make List for PieChart Data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Kills", kills),
                new PieChart.Data("Deaths", deaths),
                new PieChart.Data("Assists", assists)
        );

        final PieChart chart = new PieChart(pieChartData);

        //Add Caption to Pie Chart
        final Label caption = new Label("");
        caption.setTextFill(Color.BLACK);
        caption.setStyle("-fx-font: 24 arial;");

        StackPane piePane = new StackPane(chart, caption);
        piePane.setAlignment(Pos.CENTER);

        for (final PieChart.Data data : chart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED,
                    e -> {
                        double total = 0;
                        for (PieChart.Data d : chart.getData()) {
                            total += d.getPieValue();
                        }
                        Point2D sceneLocation = new Point2D(e.getSceneX(), e.getSceneY());
                        Point2D parentLocation = piePane.sceneToLocal(sceneLocation);

                        caption.relocate(parentLocation.getX(), parentLocation.getY());

                        String text = String.format("%.1f%%", 100 * data.getPieValue() / total);
                        caption.setText(text);
                    }
            );
            data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED,
                    e ->
                            caption.setText(""));

        }


        /**
         * Setting up JavaFX scene by adding buttons, and Vboxes
         */
        Button pieChartButton = new Button("Pie Chart");

        Button lineChartButton = new Button("Line Chart");

        VBox lineChartVbox = new VBox(5, killsLineChart, pieChartButton);
        lineChartVbox.setAlignment(Pos.CENTER);

        VBox pieChartVbox = new VBox(5, piePane, lineChartButton);
        pieChartVbox.setAlignment(Pos.CENTER);

        Scene lineChartscene = new Scene(lineChartVbox, 800, 600);
        Scene pieChartScene = new Scene(pieChartVbox, 800, 600);
        killsLineChart.getData().addAll(killsSeries, deathsSeries, assistSeries);

        primaryStage.setScene(lineChartscene);
        primaryStage.show();

        pieChartButton.setOnAction(e -> {
            primaryStage.setScene(pieChartScene);
            primaryStage.show();
        });

        lineChartButton.setOnAction(e -> {
            primaryStage.setScene(lineChartscene);
            primaryStage.show();
        });

    }
}

