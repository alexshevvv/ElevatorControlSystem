import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class ElevatorRequestGenerator extends Thread {
    private final int numFloors;
    private int lastOrderNumber = 1;
    private BlockingQueue<Order> requestsQueue;
    private List<Order> pendingRequests;

    public ElevatorRequestGenerator(int numFloors, BlockingQueue<Order> requestsQueue, List<Order> pendingRequests) {
        this.numFloors = numFloors;
        this.requestsQueue = requestsQueue;
        this.pendingRequests = pendingRequests;
    }

    @Override
    public void run() {
        Random rand = new Random();

        while (true) {
            try {
                // Генерируем случайный этаж вызова
                int sourceFloor = rand.nextInt(numFloors) + 1;

                // Вероятность выбора этажа 1 как целевого
                int targetFloor;
                if (sourceFloor != 1 && rand.nextInt(10) < 5) { // 50% вероятности того, что человек поедет на 1 этаж для реализма
                    targetFloor = 1;
                } else {
                    do {
                        targetFloor = rand.nextInt(numFloors) + 1;
                    } while (targetFloor == sourceFloor);
                }

                // Генерируем случайную массу пассажиров 80, 160 или 240
                int passengersWeight = (rand.nextInt(3) + 1) * 80;

                // Создаем новую заявку и передаем ее куда-то для обработки (например, в контроллер лифтов)
                Order request = new Order(lastOrderNumber, sourceFloor, targetFloor, passengersWeight);
                lastOrderNumber++;

                requestsQueue.put(request);
                pendingRequests.add(request);

                int delay = rand.nextInt(11) + 5;
                Thread.sleep(delay * 1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
