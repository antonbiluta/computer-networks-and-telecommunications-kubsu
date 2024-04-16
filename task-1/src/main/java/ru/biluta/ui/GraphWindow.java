package ru.biluta.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class GraphWindow extends JFrame {

    public GraphWindow(String title) {
        super(title);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void displayGraph(XYSeriesCollection dataset, String chartTitle, String xAxisLabel, String yAxisLabel) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                chartTitle,
                xAxisLabel,
                yAxisLabel,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            Color color = Color.getHSBColor((float) i / dataset.getSeriesCount(), 0.85f, 0.85f);
            renderer.setSeriesPaint(i, color);
            renderer.setSeriesShapesVisible(i, false);
        }
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        setContentPane(chartPanel);
        setVisible(true);
    }
}
