import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Cinema {
    Theatre[] theatres;
    public Cinema(Theatre[] theatres){
        this.theatres = theatres;
    }
}

class Theatre {
    Random random = new Random();
    String theatreName;
    String[] seats = new String[20];
    int availableSeats = 20;

    public Theatre(String theatreName) {
        this.theatreName = theatreName;
    }

    public void bookSeat(int numberOfSeats) {
        if (availableSeats != 0) {
            int count = 0;
            int[] customerSeats = new int[numberOfSeats];
            while(count != numberOfSeats){
                int seatNumber = random.nextInt(20);
                if (seats[seatNumber] == null) {
                    customerSeats[count] = seatNumber;
                    count ++;
                }
            }
            synchronized (this) {
                for (int i = 0; i < customerSeats.length; i++) {
                    System.out.println(Thread.currentThread().getName() + " booking seat " + customerSeats[i] + " for " + theatreName);
                }
                try {
                    Thread.sleep(random.nextInt(500) + 500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < customerSeats.length; i++) {
                    seats[customerSeats[i]] = Thread.currentThread().getName();
                    availableSeats--;
                    System.out.println(Thread.currentThread().getName() + " has booked seat " + customerSeats[i] + " for " + theatreName);
                }
            }
        }
        else{
            System.out.println(theatreName + " is full!");
        }
    }
}

class Customer implements Runnable {
    Cinema cinema;
    Random random = new Random();
    public Customer(Cinema cinema){
        this.cinema = cinema;
    }

    @Override
    public void run(){
        cinema.theatres[random.nextInt(3)].bookSeat(random.nextInt(3)+1);
    }
}

public class Main {
    public static void main(String[] args) {
        Cinema cinema = new Cinema(new Theatre[]{new Theatre("Theatre 1"), new Theatre("Theatre 2"), new Theatre("Theatre 3")});
        Thread[] threads = new Thread[10];
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Customer(cinema));
            threads[i].setName("C" + (i + 1));
            executorService.submit(threads[i]);
        }
        while (!executorService.isShutdown()) {
        }
        for(int i = 0; i < cinema.theatres.length; i++){
            System.out.println(cinema.theatres[i].theatreName + ":\n");
            for(int j = 0; j < cinema.theatres[i].seats.length; j++){
                System.out.print(cinema.theatres[i].seats[j]);
            }
        }
    }
}