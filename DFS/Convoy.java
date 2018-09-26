import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

class State {
    private ArrayList<Vehicle> vehicles;
    private State parent;
    private String description;
    private double time;
    private int depth;
    private int maxBranchingFactor;

    public State(ArrayList<Vehicle> vehicles, State parent, String description, double time, int depth, int maxBranchingFactor) {
        this.vehicles = vehicles;
        this.parent = parent;
        this.description = description;
        this.time = time;
        this.depth = depth;
        this.maxBranchingFactor = maxBranchingFactor;
    }

    public State(ArrayList<Vehicle> vehicles, State parent, String description) {
        this.vehicles = vehicles;
        this.parent = parent;
        this.description = description;
        this.time = 0;
        this.depth = 0;
        this.maxBranchingFactor = 0;
    }

    public State getParent() {
        return parent;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getMaxBranchingFactor(){
        return maxBranchingFactor;
    }

    public void setMaxBranchingFactor(int maxBranchingFactor){
        this.maxBranchingFactor = maxBranchingFactor;
    }

    public boolean isGoal() {
        return vehicles.size() == 0;
    }

    private ArrayList<Vehicle> copyVehicles(ArrayList<Vehicle> arrayList) {
        return new ArrayList<>(arrayList);
    }

    public ArrayList<State> expand(int maxLoad, int length) {
        ArrayList<State> successors = new ArrayList<>();
        ArrayList<Vehicle> newVehicles = copyVehicles(vehicles);
        int max = 0;
        int branchingFactor = 0;
        ArrayList<Integer> movedVehicles = new ArrayList<>();
        ArrayList<Integer> vehicleSpeed = new ArrayList<>();
        while (newVehicles.size() > 0) {
            Vehicle currentVehicle = newVehicles.remove(0);
            branchingFactor ++;
            movedVehicles.add(currentVehicle.getNumber());
            vehicleSpeed.add(currentVehicle.getMaxSpeed());
            max += currentVehicle.getWeight();
            if (max <= maxLoad) {
                State newState = new State(copyVehicles(newVehicles), this, "Move " + movedVehicles);
                newState.setTime(time + (double) length / Collections.min(vehicleSpeed) * 60);
                newState.setDepth(depth + 1);
                newState.setMaxBranchingFactor(Math.max(maxBranchingFactor, branchingFactor));
                successors.add(newState);
            } else {
                break;
            }
        }
        bf = successors.size();
        return successors;
    }

    @Override
    public String toString() {
        String str;
        ArrayList<Integer> numbers = new ArrayList<>();
        for (Vehicle v : vehicles) {
            numbers.add(v.getNumber());
        }
        str = isGoal() ? "None" : numbers.toString();
        str = String.format("%-20s%-20s: %s", description,
                description.equals("Initial State") ? "Vehicles" : "Remaining Vehicles", str);
        return str;
    }
}

class Vehicle {
    private int weight;
    private int maxSpeed;
    private int number;

    public Vehicle(int weight, int maxSpeed, int number) {
        this.weight = weight;
        this.maxSpeed = maxSpeed;
        this.number = number;
    }

    public int getWeight() {
        return weight;
    }

    public int getNumber() {
        return number;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    @Override
    public String toString() {
        return String.format("([%d] weight: %d, max speed: %d)", number, weight, maxSpeed);
    }
}

public class Convoy {
    private static Scanner kbd = new Scanner(System.in);
    private static ArrayList<Vehicle> vehicles = new ArrayList<>();

    public static void main(String[] args) {
        String userInput = kbd.nextLine();
        String[] userInputs = userInput.split(" ");
        int maxLoad = Integer.parseInt(userInputs[0]);
        int length = Integer.parseInt(userInputs[1]);
        int numVehicle = Integer.parseInt(userInputs[2]);
        int totalStatesVisited = 0;
        int maxFrontierSize = 1;

        for (int x = 0; x < numVehicle; x++) {
            String convoyInput = kbd.nextLine();
            String[] convoyInputs = convoyInput.split(" ");
            vehicles.add(new Vehicle(Integer.parseInt(convoyInputs[0]), Integer.parseInt(convoyInputs[1]), x + 1));
        }

        State initialState = new State(vehicles, null, "Initial State");

        ArrayList<State> frontier = new ArrayList<>();
        frontier.add(initialState);


        while (frontier.size() > 0) {
            State currentState = frontier.remove(frontier.size() - 1);
            totalStatesVisited++;
            if (currentState.isGoal()) {
                showSolution(currentState, totalStatesVisited, maxFrontierSize);
                return;
            } else {
                ArrayList<State> successorStates = currentState.expand(maxLoad, length);
                frontier.addAll(successorStates);

                maxFrontierSize = Math.max(maxFrontierSize, frontier.size());
            }
        }
        System.out.println("No Solution");
    }
    public static void showSolution(State state, int totalStatesVisited, int maxFrontierSize) {
        ArrayList<State> path = new ArrayList<>();
        while (state != null) {
            path.add(0, state);
            state = state.getParent();
        }

        System.out.println("Solution Path: ");
        for (State st : path) {
            System.out.println(st);
        }
        System.out.printf("Time Elapsed: %.1f minutes", path.get(path.size() - 1).getTime());
        System.out.printf("\nNumber of Batches: %d", path.get(path.size() - 1).getDepth());
        System.out.printf("\nMaximum Branching Factor: %d", path.get(path.size() - 1).getMaxBranchingFactor());
        System.out.printf("\nTotal States Visited: %d\n", totalStatesVisited);
        System.out.printf("Maximum Size of Frontier: %d\n", maxFrontierSize);
    }
}