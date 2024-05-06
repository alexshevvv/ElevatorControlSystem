import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public class Elevator extends Thread{
    private final int id;
    private final int maxFloor;
    private final int maxCapacity;
    private int capacity;
    private int currentFloor;
    private ElevatorState state;
    private List<Order> ordersInElevator;
    private Queue<Integer> elevatorPath;
    private boolean areDoorsOpen;
    private ElevatorSimulationPanel panel;


    private BlockingQueue<Order> requestsQueue;
    private List<Order> pendingRequests;

    public Elevator(int id, int maxCapacity, int maxFloor, BlockingQueue<Order> requestsQueue, List<Order> pendingRequests){
        this.id = id;
        this.maxFloor = maxFloor;
        this.maxCapacity = maxCapacity;
        this.capacity = 0;
        this.currentFloor = 1; // Начинаем с первого этажа
        this.state = ElevatorState.IDLE;

        this.ordersInElevator = new ArrayList<>(); // Инициализируем список заказов
        this.elevatorPath = new LinkedList<>(); // Инициализируем путь лифта

        areDoorsOpen = false;
        this.requestsQueue = requestsQueue; // Очередь всех необработанных заявок
        this.pendingRequests = pendingRequests; // Список всех заявок, котоые ещё не погружены в один из лифтов

    }

    @Override
    public void run() {
        while (true) {
            // Если путь пустой, переводим лифт в состояние IDLE

            if (elevatorPath.isEmpty()) {
                state = ElevatorState.IDLE;
                continue;
            } else {
                int nextFloor = elevatorPath.peek();

                if (nextFloor == currentFloor){
                    elevatorPath.remove();
                    processFloor();
                }else{
                    moveElevator();
                }
            }
        }
    }

    private void addToRequestsQueueIfNotExists() {
        // Проходим по всем заявкам из pendingRequests
        for (Order order : pendingRequests) {
            // Проверяем, есть ли такая заявка уже в requestsQueue
            if (!requestsQueue.contains(order)) {
                // Если заявки ещё нет в очереди, добавляем её в конец requestsQueue
                requestsQueue.add(order);
            }
        }
    }

    /**
     * Обрабатывает нахождение лифта на этаже, высаживает и загружает пассажиров
     * из лифта и в лифт
     */
    private void processFloor(){
        processDoors();
        unloadPassengers();
        loadPassengersFromPendingRequests();
        addToRequestsQueueIfNotExists();
        processDoors();
    }

    private void loadPassengersFromPendingRequests() {
        // Проходим по всем заявкам из pendingRequests
        for (int i = 0; i < pendingRequests.size(); i++) {
            Order order = pendingRequests.get(i);
            // Проверяем, начинается ли заявка на текущем этаже
            if (order.getSourceFloor() == currentFloor) {
                // Проверяем, есть ли ещё место в лифте
                if (capacity + order.getPassengersWeight() <= maxCapacity) {
                    // Загружаем пассажира в лифт
                    System.out.println("Passenger loaded at floor " + currentFloor);
                    // Увеличиваем общую загрузку лифта на массу заказа
                    capacity += order.getPassengersWeight();
                    // Удаляем заказ из pendingRequests
                    pendingRequests.remove(i);
                    // Добавляем заказ в список заказов лифта
                    ordersInElevator.add(order);

                    panel.repaint();
                    // Добавляем этаж заказа как пункт маршрута лифта
                    addPathPoint(order.getTargetFloor());
                    // Уменьшаем индекс, так как размер списка уменьшился
                    i--;
                }
            }
        }
    }

    /**
     * Метод выгружает пассажиров на данном этаже
     */
    private void unloadPassengers() {
        // Проходим по всем заявкам в лифте
        for (int i = 0; i < ordersInElevator.size(); i++) {
            Order order = ordersInElevator.get(i);
            // Проверяем, достиг ли этаж конечной точки заказа
            if (order.getTargetFloor() == currentFloor) {
                // Выгружаем пассажира и выводим лог
                System.out.println("Passengers of order #" + order.getOrderNumber() + " unloaded at floor " + currentFloor);
                // Уменьшаем общую загрузку лифта на массу заказа
                capacity -= order.getPassengersWeight();
                // Удаляем заказ из списка заказов в лифте
                ordersInElevator.remove(i);
                panel.repaint();
                // Уменьшаем индекс, так как размер списка уменьшился
                i--;
            }
        }
    }

    /**
     * Обработка дверей
     */
    private void processDoors() {
        try {
            // Проверяем текущее состояние дверей
            if (areDoorsOpen) {
                System.out.println("Elevator #" + id + " closing doors... on the floor: " + currentFloor);
                Thread.sleep(2000); // Закрытие дверей занимает 2 секунды
                areDoorsOpen = false; // Устанавливаем состояние закрытых дверей
                panel.repaint();
                System.out.println("Doors closed. ");

            } else {
                System.out.println("Elevator #" + id + " Opening doors... on the floor: " + currentFloor);
                Thread.sleep(2000); // Открытие дверей занимает 2 секунды
                panel.repaint();
                areDoorsOpen = true; // Устанавливаем состояние открытых дверей
                System.out.println("Doors opened.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод перемещает лифт на один этаж в сторону к целевому этажу
     */
    private void moveElevator() {
        if (!areDoorsOpen) {
            if (!elevatorPath.isEmpty()) {
                int nextFloor = elevatorPath.peek(); // Получаем следующий этаж из пути

                // Определяем направление движения лифта
                if (nextFloor > currentFloor) {
                    state = ElevatorState.UP;
                } else if (nextFloor < currentFloor) {
                    state = ElevatorState.DOWN;
                } else {
                    return;
                }

                // Перемещаем лифт к следующему этажу с задержкой
                try {
                    Thread.sleep(1000); // Задержка в 1.5 секунды
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                panel.repaint();

                // Перемещаем лифт к следующему этажу
                if (state == ElevatorState.UP) {
                    currentFloor++;
                } else if (state == ElevatorState.DOWN) {
                    currentFloor--;
                }


                System.out.println("Elevator #" + id + " is moved to the floor " + currentFloor);

            } else {
                state = ElevatorState.IDLE; // Если список пути пуст, переводим лифт в состояние ожидания
                System.out.println("Doors are opened!");
            }
        }
    }

    public synchronized void addPathPoint(int pathPoint){
        int pointStatus = isPointOnTheWay(pathPoint);
        switch (pointStatus){
            case 1:
                elevatorPath.add(pathPoint);
                break;
            case -1:
                elevatorPath.offer(pathPoint);
                break;
            case 0:
                break;
        }
    }

    /**
     * Функция проверяет пришедший пункт маршрута,
     * если этот пункт есть в созданном маршруте лифта, то возвращает 0,
     * если пункт маршрута находится по пути, 1; если не по пути, -1
     * @param pathPoint
     * @return
     */
    public int isPointOnTheWay(int pathPoint) {
        if (elevatorPath.isEmpty()) {
            return -1; // Если путь пустой, точно не по пути
        }

        if(elevatorPath.contains(pathPoint)){
            return 0; // Пункт маршрута находится в списке пути
        }

        int firstPoint = elevatorPath.peek();

        // Проверяем направление движения лифта
        switch (state) {
            case UP:
                // Лифт движется вверх
                if (pathPoint > currentFloor && pathPoint <= firstPoint) {
                    // Заявка находится по пути
                    return 1;
                }
                break;
            case DOWN:
                // Лифт движется вниз
                if (pathPoint < currentFloor && pathPoint >= firstPoint) {
                    // Заявка находится по пути
                    return 1;
                }
                break;
            case IDLE:
                if (pathPoint == currentFloor) {
                    return -1;
                }
                break;
            default:
                break;
        }

        // Заявка не находится по пути и не находится в текущем маршруте
        return -1;
    }

    public synchronized int getCurrentFloor() {
        return currentFloor;
    }

    public ElevatorState getElevatorState() {
        return state;
    }

    public int getElevatorId(){
        return id;
    }

    public synchronized boolean areDoorsOpen() {
        return areDoorsOpen;
    }

    public int getPathLength(){
        return elevatorPath.size();
    }

    public synchronized int getCapacity(){
        return capacity;
    }

    public synchronized int getMaxCapacity(){
        return maxCapacity;
    }

    public void setPanel(ElevatorSimulationPanel panel){
        this.panel = panel;
    }
}