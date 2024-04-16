package ru.biluta;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import ru.biluta.ui.GraphGenerator;
import ru.biluta.ui.GraphWindow;
import ru.biluta.ui.SimulationPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SimulationUI extends JFrame {
    private List<SimulationPanel> simulationPanels = new ArrayList<>();
    private JPanel simulationsContainer;
    private JTextArea infoArea;
    private List<Double> allPdenied = new ArrayList<>();
    private List<List<Double>> allPidle = new ArrayList<>();
    private JPanel chartPanel;

    private int currentId;

    public SimulationUI() {
        super("Симуляция сервера");

        currentId = 0;
        chartPanel = new JPanel();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        simulationsContainer = new JPanel();
        simulationsContainer.setLayout(new BoxLayout(simulationsContainer, BoxLayout.Y_AXIS));
        addSimulationPanel();

        JButton btnAddSimulation = new JButton("+");
        btnAddSimulation.addActionListener(e -> addSimulationPanel());

        JButton btnStartSimulations = new JButton("Старт");
        btnStartSimulations.addActionListener(e -> startSimulations());

        JButton btnClearSimulations = new JButton("Очистить");
        btnClearSimulations.addActionListener(e -> clearSimulations());

        JButton btnStartPdeniedTest = new JButton("Pотказа");
        btnStartPdeniedTest.addActionListener(e -> startPdeniedTest());

        JButton btnStartPidleTest = new JButton("Pidle");
        btnStartPidleTest.addActionListener(e -> startPidleTest());

        infoArea = new JTextArea(10, 50);
        infoArea.setEditable(false);

        JPanel controlPanel = new JPanel();
        controlPanel.add(btnAddSimulation);
        controlPanel.add(btnStartSimulations);
        controlPanel.add(btnClearSimulations);
        controlPanel.add(btnStartPdeniedTest);
        controlPanel.add(btnStartPidleTest);

        add(new JScrollPane(simulationsContainer), BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        add(new JScrollPane(infoArea), BorderLayout.NORTH);

    }

    private void addSimulationPanel() {
        currentId += 1;
        SimulationPanel panel = new SimulationPanel(currentId);
        simulationPanels.add(panel);
        simulationsContainer.add(panel);
        simulationsContainer.revalidate();
        simulationsContainer.repaint();
    }

    private void clearSimulations() {
        simulationPanels.clear();
        simulationsContainer.removeAll();
        addSimulationPanel();
        simulationsContainer.revalidate();
        simulationsContainer.repaint();
    }

    private void startPdeniedTest() {
        EventQueue.invokeLater(() -> {
            JFrame ex = new GraphGenerator(1, 1.2, 10, 1);
            ex.setVisible(true);
        });
    }

    private void startPidleTest() {
        EventQueue.invokeLater(() -> {
            JFrame ex = new GraphGenerator(1, 1.2, 10, 2);
            ex.setVisible(true);
        });
    }

    private void startSimulations() {
        XYSeriesCollection idleDataset = new XYSeriesCollection();
        XYSeries seriesPdenied = new XYSeries("Pотказа");

        for (SimulationPanel panel : simulationPanels) {
            infoArea.append("Запуск симуляции: " + panel.getParameters() + "\n");
            String name = "Тест" + panel.getId();
            Simulation simulation = new Simulation(
                    name,
                    panel.getSigma(),
                    panel.getK(),
                    panel.getLambda(),
                    panel.getBufferSize(),
                    panel.getCores(),
                    panel.getMaxTasks()
            );
            simulation.run();

            int totalDropped = simulation.getTotalTasksDropped();
            int totalGenerated = simulation.getTotalTasksGenerated();
            Double Pdenied = (double) totalDropped / totalGenerated;
            seriesPdenied.add(panel.getLambda(), Pdenied);
            allPdenied.add(Pdenied);

            List<Double> idleTimes = simulation.getIdleTimes();
            Double time = simulation.getSystemTime();
            XYSeries series = new XYSeries("Simulation " + panel.getId());
            for (int j = 0; j < idleTimes.size(); j++) {
                Double pIdle = idleTimes.get(j) / time;
                series.add(j, pIdle);

                //allPidle.add(pIdle);
            }
            idleDataset.addSeries(series);
        }
        displayChartPdenied(seriesPdenied);
        displayChartPidle(idleDataset);
        //displayCharts();
    }

    private void displayChartPdenied(XYSeries seriesPdenied) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesPdenied);

        GraphWindow graphWindow = new GraphWindow("Simulation Results");
        graphWindow.displayGraph(dataset, "Pотказа vs Lambda", "Lambda", "Pотказа");
    }

    private void displayChartPidle(XYSeriesCollection dataset) {
        GraphWindow graphWindow = new GraphWindow("Idle Time Results");
        graphWindow.displayGraph(dataset, "Idle Times vs Simulation", "Simulation", "Idle Time");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimulationUI().setVisible(true));
    }
}
