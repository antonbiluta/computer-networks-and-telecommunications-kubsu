package ru.biluta.task1.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import ru.biluta.task1.Simulation;

public class SimulationConfigPane extends HBox {
    private TextField tfSigma, tfK, tfLambda, tfBufferSize, tfCores, tfMaxTasks;

    public SimulationConfigPane() {
        tfSigma = new TextField();
        tfK = new TextField();
        tfLambda = new TextField();
        tfBufferSize = new TextField();
        tfCores = new TextField();
        tfMaxTasks = new TextField();

        this.getChildren().addAll(
                new Label("σ="), tfSigma,
                new Label("λ="), tfLambda,
                new Label("K="), tfK,
                new Label("Cores="), tfCores,
                new Label("BufferSize="), tfBufferSize,
                new Label("MaxTasks="), tfMaxTasks,
                createRemoveButton(),
                createRandomButton()
        );
        this.setPadding(new Insets(5));
        this.setSpacing(10);
    }

    private Button createRemoveButton() {
        Button btnRemove = new Button("-");
        btnRemove.setOnAction(event -> removePane());
        return btnRemove;
    }

    private Button createRandomButton() {
        Button btnRandom = new Button("Random");
        btnRandom.setOnAction(event -> fillRandomValues());
        return btnRandom;
    }

    private void fillRandomValues() {
        tfSigma.setText(String.valueOf(Math.round(Math.random() * 10000.0) / 1000.0));
        tfK.setText(String.valueOf(3));
        tfLambda.setText(String.valueOf(Math.round(Math.random() * 10000.0) / 1000.0));
        //tfBufferSize.setText(String.valueOf((int)(Math.random() * 100 + 1)));
        tfBufferSize.setText(String.valueOf(3));
        //tfCores.setText(String.valueOf((int)(Math.random() * 10 + 1)));
        tfCores.setText(String.valueOf(1));
        //tfMaxTasks.setText(String.valueOf((int)(Math.random() * 1000 + 1)));
        tfMaxTasks.setText(String.valueOf(10000));
    }

    private void removePane() {
        getParent().getChildrenUnmodifiable().remove(this);
    }

    public double getSigma() {
        return Double.parseDouble(tfSigma.getText());
    }

    public int getK() {
        return Integer.parseInt(tfK.getText());
    }

    public double getLambda() {
        return Double.parseDouble(tfLambda.getText());
    }

    public int getBufferSize() {
        return Integer.parseInt(tfBufferSize.getText());
    }

    public int getCores() {
        return Integer.parseInt(tfCores.getText());
    }

    public int getMaxTasks() {
        return Integer.parseInt(tfMaxTasks.getText());
    }
}
