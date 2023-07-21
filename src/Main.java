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
    String theatreName;
    int customerNumberOfSeats;
    int customerSeatNumber;
    Random random = new Random();
    String[] seats = new String[20];
    int availableSeats = 20;

    public Theatre(String theatreName){
        this.theatreName = theatreName;
    }

    public synchronized void bookSeat() {
        if (availableSeats != 0) {
            customerNumberOfSeats = random.nextInt(3) + 1;
            synchronized (this) {
                while (customerNumberOfSeats != 0) {
                    for (int i = 0; i < customerNumberOfSeats; i++) {
                        customerSeatNumber = random.nextInt(20);
                        if (seats[customerSeatNumber] == null) {
                            System.out.println(Thread.currentThread().getName() + " booking seat " + customerSeatNumber);
                            try {
                                Thread.sleep(random.nextInt(500) + 500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            seats[customerSeatNumber] = Thread.currentThread().getName();
                            customerNumberOfSeats--;
                            availableSeats--;
                            System.out.println(Thread.currentThread().getName() + " has booked seat " + customerSeatNumber);
                        }
                    }
                }
            }
        }
        else{
            System.out.println("Theatre is fully booked!");
        }
    }
}

class Customer implements Runnable {
    Cinema cinema;
    public Customer(Cinema cinema){
        this.cinema = cinema;
    }

    @Override
    public void run(){
//        theatre.bookSeat();
    }
}

public class Main {
    public static void main(String[] args) {
        Cinema cinema = new Cinema(new Theatre[]{new Theatre(), new Theatre(), new Theatre()});
        Thread[] threads = new Thread[10];
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Customer(cinema));
            threads[i].setName("C" + i);
            executorService.submit(threads[i]);
        }

        for(int i = 0; i < threads.length; i++){
            while(threads[i].isAlive()){

            }
            System.out.println(threads[i].getName() + " has died");
        }

        for(int i = 0; i < theatre.seats.length; i++){
            System.out.print(theatre.seats[i]);
        }
    }
}