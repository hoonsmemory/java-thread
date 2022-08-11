package thread.creation.exam2;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Thread 객체들을 만들어 각각 작업할 수 있도록 구현
 */
public class Main {

    public static final int MAX_PASSWORD = 9999;

    public static void main(String[] args) {

        Random random = new Random();

        Vault vault = new Vault(random.nextInt(MAX_PASSWORD));

        AscendingHacker ascendingHacker = new AscendingHacker(vault);
        DescendingHacker descendingHacker = new DescendingHacker(vault);
        PoliceThread policeThread = new PoliceThread();

        List<Thread> threads = Arrays.asList(ascendingHacker, descendingHacker, policeThread);

        threads.stream().forEach(thread -> thread.start());
    }

    private static class Vault {
        private int password;

        public Vault(int password) {
            this.password = password;
        }

        public boolean isCorrectPassword(int guess) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {

            }
            return this.password == guess;
        }
    }

    private static abstract class HackerThread extends Thread {
        protected Vault vault;

        public HackerThread(Vault vault) {
            this.vault = vault;
            this.setName(this.getClass().getName());
            this.setPriority(Thread.MAX_PRIORITY);
        }

        @Override
        public void start() {
            System.out.println("Starting thread " + this.getName());
            super.start();
        }
    }

    private static class AscendingHacker extends HackerThread {

        public AscendingHacker(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for(int guess = 0; guess < MAX_PASSWORD; guess++) {
                if(vault.isCorrectPassword(guess)) {
                    System.out.println(this.getName() + " guessed the password. password is " + guess);
                    System.exit(0);
                }
            }
        }
    }

    private static class DescendingHacker extends HackerThread {

        public DescendingHacker(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for(int guess = MAX_PASSWORD; guess >= 0; guess--) {
                if(vault.isCorrectPassword(guess)) {
                    System.out.println(this.getName() + " guessed the password. password is " + guess);
                    System.exit(0);
                }
            }
        }
    }

    private static class PoliceThread extends Thread {

        @Override
        public void run() {
            for(int i = 10; i >= 0; i--) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("count : " + i);
            }

            System.out.println("Game over for you hackers");
            System.exit(0);
        }
    }


}
