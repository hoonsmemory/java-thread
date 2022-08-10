package thread.interrupt.exam1;

/**
 * 일시 정지 상태의 스레드에서 InterruptedException 예외를 발생시켜 종료 상태로 갈 수 있도록 한다.
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
