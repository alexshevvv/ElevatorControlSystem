import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Main {

    private static final int FLOORS_NUMBER = 10;
    private static final int MAX_CAPACITY = 800;

    public static void main(String [] args){

        BlockingQueue<Order> requestsQueue = new LinkedBlockingQueue<>(); // Очередь необработанных сгенерированных заявок
        List<Order> pendingRequests = new CopyOnWriteArrayList<>();

        Elevator elevator1 = new Elevator(1, MAX_CAPACITY, FLOORS_NUMBER, requestsQueue, pendingRequests);
        Elevator elevator2 = new Elevator(2, MAX_CAPACITY, FLOORS_NUMBER, requestsQueue, pendingRequests);

        ElevatorRequestGenerator generator = new ElevatorRequestGenerator(FLOORS_NUMBER, requestsQueue, pendingRequests);
        Thread generatorThread = new Thread(generator);
        generatorThread.start();

        ElevatorController controller = new ElevatorController(FLOORS_NUMBER, requestsQueue, pendingRequests, elevator1, elevator2);
        Thread controllerThread = new Thread(controller);
        controllerThread.start();

        ElevatorSimulationPanel panel = new ElevatorSimulationPanel(controller, elevator1, elevator2, FLOORS_NUMBER);

        elevator1.setPanel(panel);
        elevator2.setPanel(panel);

        elevator1.start();
        elevator2.start();

        // Фиксированный размер окна
        Dimension windowSize = new Dimension(400, 800);

        JFrame frame = new JFrame("Elevator Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setPreferredSize(windowSize);
        frame.setResizable(false); // Установка окна как неразмерное
        frame.pack();
        frame.setVisible(true);

    }
}
