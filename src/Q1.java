import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;

class Cinema {
    ArrayList<Theatre> theatres;
    ArrayList<Theatre> availableTheatres = new ArrayList<Theatre>();

    public Cinema(ArrayList<Theatre> theatres) {
        this.theatres = theatres;
        for (Theatre theatre : theatres) {
            if (!theatre.isFull()) {
                availableTheatres.add(theatre);
            }
        }
    }

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

    public Theatre(String theatreName) {
        this.theatreName = theatreName;
    }

    public boolean isFull() {
        return this.isFull;
    }

    public boolean check() {
        for (String seat : seats) {
            if(seat == null){
                return true;
            }
        }
        return false;
    }

    public synchronized void bookSeat(int numberOfSeats, String customerName) {
        if (isFull) {
            return;
        }
        else{
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
//                    System.out.println(Thread.currentThread().getName() + " booking seat " + customerSeats[i] + " for " + theatreName);
                }
                for (int i = 0; i < customerSeats.size(); i++) {
                    seats[customerSeats.get(i)] = customerName;
//                    System.out.println(Thread.currentThread().getName() + " has booked seat " + customerSeats[i] + " for " + theatreName);
                }
                for (int i = 0; i < seats.length; i++){
                    if(seats[i] == null){
                        System.out.println("empty");
                    }
                    else {
                        System.out.println(seats[i]);
                    }
                }
//                System.out.println(customerName);
                for(int i = 0; i < customerSeats.size(); i++){
                    System.out.println(customerName + " booking " + customerSeats.get(i) + " at " + theatreName);
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

    public Customer(Cinema cinema, String customerName) {
        this.cinema = cinema;
        this.customerName = customerName;
    }

    @Override
    public void run() {
        ArrayList<Theatre> availableTheatres = cinema.checkAvailability();
        availableTheatres.get(random.nextInt(availableTheatres.size())).bookSeat(random.nextInt(3)+1, customerName);
        try {
            Thread.sleep(random.nextInt(500) + 500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class Q1 {
    public static void main(String[] args) {
        Cinema cinema = new Cinema(new ArrayList<Theatre>(Arrays.asList(new Theatre("Theatre 1"), new Theatre("Theatre 2"), new Theatre("Theatre 3"))));
        int numOfCustomers = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numOfCustomers);
        for (int i = 0; i < numOfCustomers; i++) {
            executorService.submit(new Customer(cinema, "C"+((i + 1))));
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            // Waiting for all tasks to complete
        }

        // Output the final status of seats in each theatre
        for (Theatre theatre : cinema.theatres) {
            System.out.println(theatre.theatreName + ":");
            for (String seat : theatre.seats) {
                System.out.print((seat != null ? seat : "-") + " ");
            }
            System.out.println();
        }
    }
}
