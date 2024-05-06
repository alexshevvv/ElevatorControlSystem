import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ElevatorController extends Thread {
    private final BlockingQueue<Order> requestsQueue;
    private List<Order> pendingRequests;
    private Elevator elevator1;
    private Elevator elevator2;
    private int maxFloor;

    public ElevatorController(int maxFloor,BlockingQueue<Order> requestsQueue, List<Order> pendingRequests, Elevator elevator1, Elevator elevator2) {
        this.requestsQueue = requestsQueue;
        this.pendingRequests = pendingRequests;
        this.elevator1 = elevator1;
        this.elevator2 = elevator2;
        this.maxFloor = maxFloor;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Получаем первую заявку из очереди (блокирующая операция, ждем пока не появится заявка)
                Order request = requestsQueue.take();
                // Обработка заявки
                processRequest(request);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processRequest(Order order) throws InterruptedException {
        Elevator suitableElevator = getSuitableElevator(order, elevator1, elevator2);

        System.out.print(order.toString());
        System.out.println(" Is added to elevator #" + suitableElevator.getElevatorId());

        suitableElevator.addPathPoint(order.getSourceFloor());
    }

    private Elevator getSuitableElevator(Order order, Elevator elevator1, Elevator elevator2){

        if(elevator1.getElevatorState() == ElevatorState.IDLE && elevator2.getElevatorState() == ElevatorState.IDLE){ // Оба лифта свободны
            Elevator nearestElevator = getNearestElevator(order, elevator1, elevator2);


            if (nearestElevator == null) {
                return elevator1;
            }
            return nearestElevator;

        } else if(elevator1.getElevatorState() != ElevatorState.IDLE && elevator2.getElevatorState() == ElevatorState.IDLE){ // Свободен лифт 2

            int elevator1WayState = elevator1.isPointOnTheWay(order.getSourceFloor());

            if(elevator1WayState == 0 || elevator1WayState == 1){ // Если маршрут находится по пути, или уже есть в списке точек лифта 1
                return  elevator1;
            }else{
                return elevator2;
            }

        } else if (elevator1.getElevatorState() == ElevatorState.IDLE && elevator2.getElevatorState() != ElevatorState.IDLE){ // Свободен лифт 1

            int elevator2WayState = elevator2.isPointOnTheWay(order.getSourceFloor());

            if(elevator2WayState == 0 || elevator2WayState == 1){ // Если маршрут находится по пути, или уже есть в списке точек лифта 1

                if(elevator2WayState == 0 || elevator2WayState == 1){ // Если маршрут находится по пути, или уже есть в списке точек лифта 2
                    return  elevator2;
                }else{
                    return elevator1;
                }

            }else{
                return elevator2;
            }

        } else { // Оба лифта заняты
            int elevator1WayState = elevator1.isPointOnTheWay(order.getSourceFloor());
            int elevator2WayState = elevator2.isPointOnTheWay(order.getSourceFloor());

            if(elevator1WayState == elevator2WayState){
                return elevator1;
            }else if(elevator1WayState > elevator2WayState){
                return elevator1;
            }else{
                return elevator2;
            }
        }
    }

    public Elevator getNearestElevator(Order order, Elevator elevator1, Elevator elevator2) {
        int distanceToElevator1 = Math.abs(elevator1.getCurrentFloor() - order.getSourceFloor());
        int distanceToElevator2 = Math.abs(elevator2.getCurrentFloor() - order.getSourceFloor());

        // Выбираем ближайший лифт
        if (distanceToElevator1 < distanceToElevator2) {
            return elevator1; // Лифт 1 ближе
        } else if (distanceToElevator2 < distanceToElevator1) {
            return elevator2; // Лифт 2 ближе
        } else {
            return null; // Лифты находятся на одинаковом расстоянии
        }
    }

    public List<Boolean> getFloorsOrders() {
        List<Boolean> ordersList = new ArrayList<>();

        // Инициализируем список значением false для каждого этажа
        for (int i = 0; i < maxFloor; i++) {
            ordersList.add(false);
        }

        // Проходим по всем заявкам в очереди и помечаем этажи, на которые есть заявки, значением true
        for (Order order : pendingRequests) {
            int sourceFloor = order.getSourceFloor();
            ordersList.set(sourceFloor - 1, true); // Устанавливаем значение true для этажа (нумерация от 0 до floors - 1)
        }

        return ordersList;
    }


}
