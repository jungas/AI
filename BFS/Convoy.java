import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

class State {
    private ArrayList<Vehicle> vehicles;
    private State parent;
    private String description;
    private double time;

    public State(ArrayList<Vehicle> vehicles, State parent, String description) {
        this.vehicles = vehicles;
        this.parent = parent;
        this.description = description;
        this.time = 0;
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
        ArrayList<Integer> movedVehicles = new ArrayList<>();
        ArrayList<Integer> vehicleSpeed = new ArrayList<>();
        while (newVehicles.size() > 0) {
            Vehicle currentVehicle = newVehicles.remove(0);
            movedVehicles.add(currentVehicle.getNumber());
            vehicleSpeed.add(currentVehicle.getMaxSpeed());
            max += currentVehicle.getWeight();
            if (max <= maxLoad) {
                State newState = new State(copyVehicles(newVehicles), this, "Move " + movedVehicles);
                newState.setTime(time + (double) length / Collections.min(vehicleSpeed) * 60);
                // System.out.println(newState);
                successors.add(newState);
            } else {
                break;
            }
        }
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
        str = String.format("%-20s%-20s: %s", description, description.equals("Initial State") ? "Vehicles" :
                "Remaining Vehicles", str);
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

        for (int x = 0; x < numVehicle; x++) {
            String convoyInput = kbd.nextLine();
            String[] convoyInputs = convoyInput.split(" ");
            vehicles.add(new Vehicle(Integer.parseInt(convoyInputs[0]), Integer.parseInt(convoyInputs[1]), x + 1));
        }

        State initialState = new State(vehicles, null, "Initial State");

        ArrayList<State> frontier = new ArrayList<>();
        frontier.add(initialState);

        ArrayList<State> goalStates = new ArrayList<>();

        while (frontier.size() > 0) {
            State currentState = frontier.remove(0);
            if (currentState.isGoal()) {
                //showSolution(currentState);
                goalStates.add(currentState);
                //System.out.println("\n\n-------------\n");
                //return;
            } else {
                ArrayList<State> successorStates = currentState.expand(maxLoad, length);
                frontier.addAll(successorStates);
            }
        }
        showOptimalSolution(goalStates);
        //System.out.println("No Solution");
    }

    public static void showSolution(State state) {
        ArrayList<State> path = new ArrayList<>();
        while (state != null) {
            path.add(0, state);
            state = state.getParent();
        }

        System.out.println("Solution Path: ");
        for (State st : path) {
            System.out.println(st);
        }
        System.out.printf("Time Elapsed: %.1f minutes", path.get(path.size()-1).getTime());
    }

    public static void showOptimalSolution(ArrayList<State> states){
        State currentGoal = states.get(0);
        for (State goal: states) {
            if (currentGoal.getTime() > goal.getTime()) {
                currentGoal = goal;
            }
        }
        showSolution(currentGoal);
    }
}