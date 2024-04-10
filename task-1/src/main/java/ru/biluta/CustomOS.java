package ru.biluta;

public class CustomOS {

    public static void main(String[] args) {
        Simulation simulation1 = new Simulation("test1", 1, 3, 2, 3, 1, 10000);
        simulation1.run();

        Simulation simulation2 = new Simulation("test2", 2, 3, 1, 3, 1, 10000);
        simulation2.run();

        Simulation simulation3 = new Simulation("test3", 6, 3, 2, 3, 1, 10000);
        simulation3.run();

        Simulation simulation4 = new Simulation("test4", 1, 3, 6, 3, 4, 10000);
        simulation4.run();

        Simulation simulation5 = new Simulation("test5", 2, 3, 1, 3, 3, 10000);
        simulation5.run();
    }

}
