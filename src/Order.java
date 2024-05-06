public class Order {
    private final int sourceFloor;
    private final int targetFloor;
    private final int passengersWeight;
    private final int orderNumber;

    public Order(int orderNumber, int sourceFloor, int targetFloor, int passengersWeight) {
        this.sourceFloor = sourceFloor;
        this.targetFloor = targetFloor;
        this.passengersWeight = passengersWeight;
        this.orderNumber = orderNumber;
    }

    public int getSourceFloor() {
        return sourceFloor;
    }

    public int getTargetFloor() {
        return targetFloor;
    }

    public int getPassengersWeight() {
        return passengersWeight;
    }

    @Override
    public String toString() {
        return "Order #" + orderNumber + ", Source floor: " + sourceFloor + ", Target floor: " + targetFloor;
    }

    public int getOrderNumber(){
        return orderNumber;
    }
}
