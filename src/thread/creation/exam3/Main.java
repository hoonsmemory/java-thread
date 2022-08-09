package thread.creation.exam3;

/**
 * 인터럽트한 스레드가 이미 인터럽트 당했을 경우 InterruptedException을 발생시킨다.
 */
public class Main {

    public static void main(String[] args) {
        Thread thread = new Thread(new BlockingTask());
        thread.start();

        //InterruptedException 발생하고 종료.
        thread.interrupt();
    }

    private static class BlockingTask implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(500000);
            } catch (InterruptedException e) {
                System.out.println("Exiting blocking thread");
            }
        }
    }
}
