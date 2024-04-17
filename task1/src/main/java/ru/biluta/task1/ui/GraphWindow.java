package ru.biluta.task1.ui;

import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GraphWindow extends Stage {

    public GraphWindow(String title) {
        setTitle(title);
    }

    public void displayGraph(LineChart<Number, Number>... lineChart) {
        VBox vbox = new VBox(lineChart);
        Scene scene = new Scene(vbox);
        setScene(scene);
        show();
    }

    public void displayGraph(XYChart.Series<Number, Number> seriesPdenied,
                             XYChart.Series<Number, Number> seriesPidle,
                             XYChart.Series<Number, Number>[] multPidleSeries) {

        LineChart<Number, Number> lineChartPdenied = createChart(
                "Pотказа в зависимости от λ",
                "λ", "Pотказа",
                seriesPdenied
        );
        LineChart<Number, Number> lineChartPidle = createChart(
                "Pidle в зависимости от λ",
                null, null,
                seriesPidle
        );
        LineChart<Number, Number> lineChartMultPdenied = createChart(
                "Время простоя",
                "единица",
                "Время",
                null,
                multPidleSeries
        );

        VBox vbox = new VBox(lineChartPdenied, lineChartPidle, lineChartMultPdenied);
        Scene scene = new Scene(vbox);
        setScene(scene);
        show();
    }

    @SafeVarargs
    public final LineChart<Number, Number> createChart(String title,
                                                       String xLabel,
                                                       String yLabel,
                                                       XYChart.Series<Number, Number> series,
                                                       XYChart.Series<Number, Number>... serieses) {
        NumberAxis xAxis = new NumberAxis();
        if (xLabel != null) {
            xAxis.setLabel(xLabel);
        }
        NumberAxis yAxis = new NumberAxis();
        if (yLabel != null) {
            yAxis.setLabel(yLabel);
        }
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        if (series != null) {
            lineChart.getData().add(series);
        }
        if (serieses != null) {
            lineChart.getData().addAll(serieses);
        }
        lineChart.setTitle(title);
        return lineChart;
    }

}
