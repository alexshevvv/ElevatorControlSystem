import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ElevatorSimulationPanel extends JPanel {
    private static int floors;
    private static final int ANIMATION_DURATION = 2000;
    private List<Elevator> elevators;

    private Elevator elevator1;
    private Elevator elevator2;
    private Graphics g;
    private int floorHeight = 10;
    private ElevatorController elevatorController;


    public ElevatorSimulationPanel(ElevatorController elevatorController, Elevator elevator1, Elevator elevator2, int floors) {
        this.elevator1 = elevator1;
        this.elevator2 = elevator2;
        this.floors = floors;
        this.elevatorController = elevatorController;

        elevators = new ArrayList<>();
        elevators.add(elevator1);
        elevators.add(elevator2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        this.g = g;
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();
        int floorHeight = height / floors;
        this.floorHeight = floorHeight;

        List<Boolean> floorsOrders = elevatorController.getFloorsOrders();
        // Отрисовка этажей
        for (int i = 1; i <= floors; i++) {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, (floors - i) * floorHeight, width, floorHeight);
            g.setColor(Color.BLACK);
            g.drawString("Floor " + i, 10, (floors - i + 1) * floorHeight - 10);

            if(floorsOrders.get(i - 1)){
                g.setColor(Color.RED);
            }else{
                g.setColor(Color.BLACK);
            }
            g.drawOval(20, (floors - i + 1) * floorHeight - 35, 10, 10);
        }

        // Отрисовка лифтов
        for (Elevator elevator : elevators) {
            int elevatorY = (floors - elevator.getCurrentFloor()) * floorHeight;
            if(elevator.areDoorsOpen()){
                g.setColor(Color.YELLOW);
            }else{
                g.setColor(Color.GRAY);
            }
            g.fillRect(70 * elevator.getElevatorId(), elevatorY, 60, floorHeight);
            g.setColor(Color.BLACK);
            g.drawString(Integer.toString(elevator.getCapacity()) + "/" + Integer.toString(elevator.getMaxCapacity()), 70 * elevator.getElevatorId(), elevatorY + floorHeight/2);
        }

        // Отрисовка текста над всеми шахтами
        g.setColor(Color.BLACK);
        g.drawString("Elevator 1", 80, 20);
        g.drawString("Elevator 2", 150, 20);

    }




}