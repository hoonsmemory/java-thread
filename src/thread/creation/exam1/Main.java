package thread.creation.exam1;

public class Main {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
            System.out.println("This thread's name is " + Thread.currentThread().getName());
            throw new RuntimeException("Occurred RuntimeException !!");
        });

        thread.setName("Test Thread"); //스레드 이름 설정
        thread.setPriority(Thread.MAX_PRIORITY); //스레드 우선순위 설정

        //예외 핸들러 지정
        thread.setUncaughtExceptionHandler((t, e) -> {
            System.out.println("A critical error happened in thread " + t.getName() +
                    " the error is " + e.getMessage());
        });

        thread.start();

    }

}
