package ru.biluta.task1.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class TestConfigPane extends HBox {
    private TextField countTest, lambdaStart, lambdaEnd, lambdaIncrement;

    public TestConfigPane() {
        countTest = new TextField();
        lambdaStart = new TextField();
        lambdaEnd = new TextField();
        lambdaIncrement = new TextField();
        lambdaIncrement.setEditable(false);

        countTest.focusedProperty().addListener(createListener());
        lambdaStart.focusedProperty().addListener(createListener());
        lambdaEnd.focusedProperty().addListener(createListener());
        lambdaIncrement.focusedProperty().addListener(createListener());

        this.getChildren().addAll(
                new Label("кол-во тестов "), countTest,
                new Label("λ0="), lambdaStart,
                new Label("λn="), lambdaEnd,
                new Label("λ increment="), lambdaIncrement
        );
        this.setPadding(new Insets(5));
        this.setSpacing(10);
    }

    private ChangeListener<Boolean> createListener() {
        return (arg0, oldPropertyValue, newPropertyValue) -> {
            setLambdaIncrement();
        };
    }

    private void setLambdaIncrement() {

        String startString = lambdaStart.getText();
        String endString = lambdaEnd.getText();
        String countString = countTest.getText();

        if (startString.isEmpty() || endString.isEmpty() || countString.isEmpty()) {
            return;
        }

        double start = Double.parseDouble(startString);
        double end = Double.parseDouble(endString);
        int count = Integer.parseInt(countString);
        double increment = (end - start) / (count - 1);

        lambdaIncrement.setText(String.valueOf(increment));
    }

    public int getCountTest() {
        return Integer.parseInt(countTest.getText());
    }

    public double getLambdaStart() {
        return Double.parseDouble(lambdaStart.getText());
    }

    public double getLambdaEnd() {
        return Double.parseDouble(lambdaEnd.getText());
    }

    public double getLambdaIncrement() {
        return Double.parseDouble(lambdaIncrement.getText());
    }

    public double calculateLambda(int index) {
        double increment = getLambdaIncrement();
        double start = getLambdaStart();
        return start + (index * increment);
    }
}
