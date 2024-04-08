package ru.biluta;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimulationGUI extends JFrame {
    private final JTextField bufferSizeInput = new JTextField(5);
    private final JButton startSimulationButton = new JButton("Start Simulation");

    public SimulationGUI() {
        super("Server Simulation");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(new JLabel("Buffer Size:"));
        add(bufferSizeInput);
        add(startSimulationButton);

        startSimulationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int bufferSize = Integer.parseInt(bufferSizeInput.getText());
                startSimulation(bufferSize);
            }
        });
    }

    private void startSimulation(int bufferSize) {
        System.out.println("Simulation started with buffer size: " + bufferSize);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SimulationGUI().setVisible(true);
            }
        });
    }
}
