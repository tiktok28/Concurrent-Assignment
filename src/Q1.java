import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;

class Cinema {
    ArrayList<Theatre> theatres;
    ArrayList<Theatre> availableTheatres = new ArrayList<Theatre>();

    // Constructor to initialize Cinema with a list of theatres
    public Cinema(ArrayList<Theatre> theatres) {
        this.theatres = theatres;
        for (Theatre theatre : theatres) {
            if (!theatre.isFull()) {
                availableTheatres.add(theatre);
            }
        }
    }

    // Method to check and return available theatres
    public synchronized ArrayList<Theatre> checkAvailability() {
        availableTheatres.clear();
        for (Theatre theatre : theatres) {
            if (!theatre.isFull()) {
                availableTheatres.add(theatre);
            }
        }
        return availableTheatres;
    }
}

class Theatre {
    Random random = new Random();
    String theatreName;
    String[] seats = new String[20];
    int availableSeats = 20;
    private boolean isFull = false;

    // Constructor to initialize a Theatre with a name
    public Theatre(String theatreName) {
        this.theatreName = theatreName;
    }

    // Method to check if the theatre is full
    public boolean isFull() {
        return this.isFull;
    }

    // Method to check if there are any available seats in the theatre
    public boolean check() {
        for (String seat : seats) {
            if(seat == null){
                return true;
            }
        }
        return false;
    }

    // Method to book seats for a customer
    public synchronized void bookSeat(int numberOfSeats, String customerName) {
        if (isFull) {
            return;
        } else {
            if (availableSeats != 0) {
                int count = 0;
                if(numberOfSeats > availableSeats){
                    numberOfSeats = availableSeats;
                }
                ArrayList<Integer> customerSeats = new ArrayList<Integer>();
                while (count != numberOfSeats) {
                    if(!check()){
                        System.out.println("No seat available");
                        return;
                    }
                    int seatNumber = random.nextInt(20);
                    if (seats[seatNumber] == null && !customerSeats.contains(seatNumber)) {
                        customerSeats.add(seatNumber);
                        count++;
                    }
                }
                availableSeats = availableSeats - numberOfSeats;
                for (int i = 0; i < customerSeats.size(); i++) {
                    System.out.println(customerName + " booking seat " + customerSeats.get(i) + " for " + theatreName);
                }
                for (int i = 0; i < customerSeats.size(); i++) {
                    seats[customerSeats.get(i)] = customerName;
                }
            } else {
                isFull = true;
            }
        }
    }
}

class Customer implements Runnable {
    Cinema cinema;
    String customerName;
    Random random = new Random();

    // Constructor to initialize a Customer with the Cinema and a name
    public Customer(Cinema cinema, String customerName) {
        this.cinema = cinema;
        this.customerName = customerName;
    }

    @Override
    public void run() {
        // Check available theatres and book seats for the customer
        ArrayList<Theatre> availableTheatres = cinema.checkAvailability();
        availableTheatres.get(random.nextInt(availableTheatres.size())).bookSeat(random.nextInt(3)+1, customerName);
        try {
            Thread.sleep(random.nextInt(500) + 500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Display the seats booked by the customer in each theatre
        for(Theatre theatre : cinema.theatres){
            int index = 0;
            for(String seat : theatre.seats){
                if(seat == customerName){
                    System.out.println(customerName + " booked " + index + " at " + theatre.theatreName);
                }
                index++;
            }
        }
    }
}

public class Q1 {
    public static void main(String[] args) {
        // Initialize Cinema with a list of theatres
        Cinema cinema = new Cinema(new ArrayList<Theatre>(Arrays.asList(new Theatre("Theatre 1"), new Theatre("Theatre 2"), new Theatre("Theatre 3"))));
        int numOfCustomers = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numOfCustomers);

        // Create and submit customer threads to the ExecutorService
        for (int i = 0; i < numOfCustomers; i++) {
            executorService.submit(new Customer(cinema, "C"+((i + 1))));
        }

        // Shutdown the ExecutorService and wait for all tasks to complete
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            // Waiting for all tasks to complete
        }

        // Output the final status of seats in each theatre
        for (Theatre theatre : cinema.theatres) {
            System.out.println(theatre.theatreName + ":");
            for (String seat : theatre.seats) {
                if(seat == null){
                    System.out.print("-");
                    System.out.print(" ");
                }
                else{
                    System.out.print(seat);
                    System.out.print(" ");
                }
            }
            System.out.println("");
        }
    }
}
