package ru.biluta.task1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ru.biluta.task1.ui.GraphWindow;
import ru.biluta.task1.ui.SimulationConfigPane;
import ru.biluta.task1.ui.TestConfigPane;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class SimulationApp extends Application {

    private List<SimulationConfigPane> simulationPanes = new ArrayList<>();
    private TestConfigPane testConfig = new TestConfigPane();
    private VBox simulationsContainer;
    private VBox testContainer;
    private TextArea consoleArea;
    private List<Simulation> simulations = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        simulationsContainer = new VBox(10);
        simulationsContainer.setAlignment(Pos.TOP_CENTER);
        addPane();

        testContainer = new VBox(10);
        testContainer.setAlignment(Pos.CENTER);
        testContainer.getChildren().add(testConfig);

        ScrollPane scrollPane = new ScrollPane(simulationsContainer);
        scrollPane.setFitToWidth(true);

        consoleArea = new TextArea();
        consoleArea.setEditable(false);

        Button btnStart = new Button("Start");
        btnStart.setOnAction(event -> startSimulations());

        Button btnAdd = new Button("Add");
        btnAdd.setOnAction(event -> addPane());

        Button btnClear = new Button("Clear");
        btnClear.setOnAction(event -> clearSimulations());

        Button btnTest = new Button("Запустить тест");
        btnTest.setOnAction(event -> startTest());

        HBox controlBox = new HBox(10, btnStart, btnAdd, btnClear);
        controlBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(10, scrollPane, controlBox, testContainer, btnTest, consoleArea);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(root, 1400, 600);
        stage.setTitle("Simulation Interface");
        stage.setScene(scene);
        stage.show();
    }

    private void addPane() {
        SimulationConfigPane pane = createSimulationConfigPane();
        simulationsContainer.getChildren().add(pane);
    }

    private SimulationConfigPane createSimulationConfigPane() {
        SimulationConfigPane pane = new SimulationConfigPane();
        simulationPanes.add(pane);
        return pane;
    }

    private void startTest() {
        consoleArea.clear();
        simulations.clear();

        List<XYChart.Series<Number, Number>> multipleSeriesPdenied = new ArrayList<>();
        List<XYChart.Series<Number, Number>> multipleSeriesPidle = new ArrayList<>();

        for (SimulationConfigPane pane : simulationPanes) {
            double sigma = pane.getSigma();
            int k = pane.getK();
            int countTest = testConfig.getCountTest();

            String simulationName = "Simulation" + (simulationPanes.indexOf(pane) + 1);

            XYChart.Series<Number, Number> seriesPdenied = new XYChart.Series<>();
            XYChart.Series<Number, Number> seriesPidle = new XYChart.Series<>();
            seriesPdenied.setName(simulationName + "(σ="+sigma+")");
            seriesPidle.setName(simulationName + "(σ="+sigma+")");

            for (int i = 0; i < countTest; i++) {
                double lambda = testConfig.calculateLambda(i);
                Simulation simulation = new Simulation(simulationName,
                        sigma, k, lambda,
                        pane.getBufferSize(), pane.getCores(), pane.getMaxTasks()
                );
                simulation.run();

                double Pdenied = (double) simulation.getTotalTasksDropped() / simulation.getTotalTasksGenerated();
                double Pidle = simulation.getCalculateIdleTime() / simulation.getSystemTime();

                simulations.add(simulation);

                XYChart.Data<Number, Number> data1 = new XYChart.Data<>(lambda, Pdenied);
                XYChart.Data<Number, Number> data2 = new XYChart.Data<>(lambda, Pidle);
                seriesPdenied.getData().add(data1);
                seriesPidle.getData().add(data2);
            }

            multipleSeriesPdenied.add(seriesPdenied);
            multipleSeriesPidle.add(seriesPidle);
        }

        GraphWindow graphWindow = new GraphWindow("Статистика за проведенный тест");
        LineChart<Number, Number> chartDenied = graphWindow.createChart(
                "Pотказа в зависимости от λ",
                "λ",
                "Pотказа",
                null,
                multipleSeriesPdenied.toArray(XYChart.Series[]::new)
        );
        LineChart<Number, Number> chartIdle = graphWindow.createChart(
                "Pidle в зависимости от λ",
                "λ",
                "Pidle",
                null,
                multipleSeriesPidle.toArray(XYChart.Series[]::new)
        );
        graphWindow.displayGraph(chartDenied, chartIdle);

    }

    private void startSimulations() {
        consoleArea.clear();

        XYChart.Series<Number, Number> seriesPdenied = new XYChart.Series<>();
        seriesPdenied.setName("Общая статистика Pотказа");

        XYChart.Series<Number, Number> seriesPidle = new XYChart.Series<>();
        seriesPidle.setName("Общая статистика Pidle");

        List<XYChart.Series<Number, Number>> multipleSeriesPdenied = new ArrayList<>();

        for (SimulationConfigPane pane : simulationPanes) {
            double sigma = pane.getSigma();
            int k = pane.getK();
            double lambda = pane.getLambda();
            int bufferSize = pane.getBufferSize();
            int cores = pane.getCores();
            int maxTasks = pane.getMaxTasks();
            int indexPane = simulationPanes.indexOf(pane) + 1;

            String hostname = "server" + indexPane + "@bilutaEmulator:";
            String server = "[SERVER" + indexPane + "]: ";

            consoleArea.appendText(
                    hostname + " simulation run"
                            + " --sigma=" + sigma
                            + " --k=" + k
                            + " --lambda=" + lambda
                            + "\n"
            );

            Simulation simulation = new Simulation(
                    "Simulation " + indexPane,
                    sigma, k, lambda, bufferSize, cores, maxTasks
            );

            consoleArea.appendText(server + "Started Work\n");

            simulation.run();

            int totalGenerated = simulation.getTotalTasksGenerated();
            int totalDenied = simulation.getTotalTasksDropped();
            double Pdenied = (double) totalDenied / totalGenerated;
            double Pidle = simulation.getCalculateIdleTime() / simulation.getSystemTime();
            double systemTime = simulation.getSystemTime();
            consoleArea.appendText(server + "Всего сгенерировано задач: " + totalGenerated + "\n");
            consoleArea.appendText(server + "Всего отказов: " + totalDenied + "\n");
            consoleArea.appendText(server + "Pотказа: " + Pdenied + "\n");
            consoleArea.appendText(server + "Pidle: " + Pidle + "\n");
            consoleArea.appendText(server + "Shutdown..." + "\n");
            consoleArea.appendText(server + "Время системы на момент выключения: " + systemTime + "\n");


            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Simulation" + indexPane + "(σ="+sigma+", λ="+lambda+")");
            List<Double> idleTimes = simulation.getIdleTimes();
            for (double idleTime : idleTimes) {
                XYChart.Data<Number, Number> data = new XYChart.Data<>(idleTimes.indexOf(idleTime), idleTime);
                series.getData().add(data);
            }
            multipleSeriesPdenied.add(series);

            Platform.runLater(() -> {
                seriesPdenied.getData().add(new XYChart.Data<>(lambda, Pdenied));
                seriesPidle.getData().add(new XYChart.Data<>(lambda, Pidle));
            });
        }
        XYChart.Series<Number, Number>[] multSeries = multipleSeriesPdenied.toArray(XYChart.Series[]::new);

        GraphWindow graphWindow = new GraphWindow("Статистика выполнения");
        graphWindow.displayGraph(seriesPdenied, seriesPidle, multSeries);
    }

    private void clearSimulations() {
        simulationPanes.clear();
        simulationsContainer.getChildren().clear();
        addPane();
    }

}
