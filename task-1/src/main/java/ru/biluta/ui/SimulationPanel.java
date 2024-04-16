package ru.biluta.ui;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class SimulationPanel extends JPanel {

    @Getter
    private int id;
    private JTextField tfSigma;
    private JTextField tfK;
    private JTextField tfLambda;
    private JTextField tfBufferSize;
    private JTextField tfCores;
    private JTextField tfMaxTasks;

    public SimulationPanel(int extId) {
        this.id = extId;
        setLayout(new FlowLayout());
        add(new JLabel("Sigma:"));
        tfSigma = new JTextField(5);
        add(tfSigma);
        add(new JLabel("K:"));
        tfK = new JTextField(5);
        add(tfK);
        add(new JLabel("Lambda:"));
        tfLambda = new JTextField(5);
        add(tfLambda);
        add(new JLabel("Buffer Size:"));
        tfBufferSize = new JTextField(5);
        add(tfBufferSize);
        add(new JLabel("Cores:"));
        tfCores = new JTextField(5);
        add(tfCores);
        add(new JLabel("Max Tasks:"));
        tfMaxTasks = new JTextField(5);
        add(tfMaxTasks);
    }

    public String getParameters() {
        return "Sigma: " + tfSigma.getText()
                + ", K: " + tfK.getText()
                + ", Lambda: " + tfLambda.getText()
                + ", Buffer Size: " + tfBufferSize.getText()
                + ", Cores: " + tfCores.getText()
                + ", Max Tasks: " + tfMaxTasks.getText();
    }

    public Double getSigma() {
        return Double.parseDouble(tfSigma.getText());
    }

    public Integer getK() {
        return Integer.parseInt(tfK.getText());
    }

    public Double getLambda() {
        return Double.parseDouble(tfLambda.getText());
    }

    public Integer getBufferSize() {
        return Integer.parseInt(tfBufferSize.getText());
    }

    public Integer getCores() {
        return Integer.parseInt(tfCores.getText());
    }

    public Integer getMaxTasks() {
        return Integer.parseInt(tfMaxTasks.getText());
    }
}
