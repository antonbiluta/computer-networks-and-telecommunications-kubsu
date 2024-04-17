//package ru.biluta.task1.ui;
//
//import javafx.stage.Stage;
//import ru.biluta.task1.Simulation;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class GraphGenerator extends Stage {
//    private final int NUM_LAMBDA_TESTS;
//
//    private double[] sigmas = {0.01, 0.1, 1, 1.5, 1.25, 1.1, 1.2, 0.8, 2};
//    private int[] ks = {3, 3, 3, 3, 3, 3};
//    private double lambdaStart;
//    private double lambdaEnd;
//    private double lambdaIncrement;
//
//    private ArrayList<Simulation> simulations;
//
//    public GraphGenerator(double start, double end, int countTest, int testNumber) {
//        lambdaStart = start;
//        lambdaEnd = end;
//        lambdaIncrement = (lambdaEnd - lambdaStart) / (countTest - 1);
//        NUM_LAMBDA_TESTS = countTest;
//        simulations = new ArrayList<>();
//        generateSimulations();
//        switch (testNumber) {
//            case 1: {
//                generatePdenied();
//                break;
//            }
//            case 2: {
//                generatePidle();
//                break;
//            }
//        }
//    }
//
//    public void generatePdenied() {
//        XYSeriesCollection dataset = createPdeniedDataset();
//        JFreeChart chart = createChart(dataset, "Pотказа");
//        ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setBorder(null);
//        add(chartPanel);
//        pack();
//        setTitle("Pотказа и λ");
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//    }
//
//    public void generatePidle() {
//        XYSeriesCollection dataset = createPidleDataset();
//        JFreeChart chart = createChart(dataset, "Pidle");
//        ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setBorder(null);
//        add(chartPanel);
//        pack();
//        setTitle("Pidle и λ");
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//    }
//
//    private XYSeriesCollection createPidleDataset() {
//        XYSeriesCollection dataset = new XYSeriesCollection();
//        for (int i = 0; i < sigmas.length; i++) {
//            XYSeries series = new XYSeries("μ = " + ks[i] + ", σ = " + sigmas[i]);
//            for (Simulation simulation : simulations) {
//                if (simulation.getSigma() != sigmas[i]) continue;
//                List<Double> times = simulation.getIdleTimes();
//                double idleTime = simulation.getCalculateIdleTime();
//                Double systemTime = simulation.getSystemTime();
//                series.add(simulation.getLambda(), idleTime / systemTime);
//            }
//            dataset.addSeries(series);
//        }
//        return dataset;
//    }
//
//    private XYSeriesCollection createPdeniedDataset() {
//        XYSeriesCollection dataset = new XYSeriesCollection();
//        for (int i = 0; i < sigmas.length; i++) {
//            XYSeries series = new XYSeries("μ = " + ks[i] + ", σ = " + sigmas[i]);
//            for (Simulation simulation : simulations) {
//                if (simulation.getSigma() != sigmas[i]) continue;
//                double pDenied = (double) simulation.getTotalTasksDropped() / simulation.getTotalTasksGenerated();
//                series.add(simulation.getLambda(), pDenied);
//            }
//            dataset.addSeries(series);
//        }
//        return dataset;
//    }
//
//    private void generateSimulations() {
//        for (int i = 0; i < sigmas.length; i++) {
//            for (int j = 0; j < NUM_LAMBDA_TESTS; j++) {
//                double lambda = lambdaStart + (j * lambdaIncrement);
//                Simulation simulation = new Simulation(
//                        "",
//                        sigmas[i], ks[i], lambda,
//                        3, 1, 10000);
//                simulation.run();
//                simulations.add(simulation);
//            }
//        }
//    }
//
//    private JFreeChart createChart(
//            final XYSeriesCollection dataset,
//            String title
//    ) {
//        return ChartFactory.createXYLineChart(
//                title + " vs λ",
//                "λ",
//                title,
//                dataset
//        );
//    }
//}
