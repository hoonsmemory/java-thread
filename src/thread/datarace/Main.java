package thread.datarace;

/**
 * Data Race 문제
 *
 * 이론상 increase() 메서드에서 x는 y보다 먼저 ++ 되므로 y는 x보다 큰 수가 될 수 없다.
 * 하지만 종종 컴파일러와 CPU 가 성능 최적화와 하드웨어 활용을 위해 비순차적으로 명령을 처리하는 경우가 있다.
 *
 * Data Race 를 피할 수 있는 방법
 * 1. synchronized 키워드 사용
 * synchronized 키워드를 사용해서 동시 실행에 대응하고 읽기, 쓰기 혹은 공유 변수로부터 보호할 수 있다.
 * synchronized 키워드가 선언된 메서드를 재정렬해도 문제가 되지 않으며, 하나의 스레드만 공유 변수에 접근할 수 있고 Data Race 는 일어나지 않는다.
 * 하지만 예시를 통해 본 것처럼 Race Condition 이 아니므로 두 메서드에 synchronize 키워드로 동시 실행을 막지 않아도 된다.
 *
 * 2. volatile 키워드 사용
 *  volatile 키워드는 blocking overhead 를 줄이고, 처리 순서를 보장한다.
 *  특히 전역 변수에 volatile 을 선언하면
 *  volatile 변수 접근 전 코드가 접근 명령을 수행하기 전에 실행되도록 하고, 접근 명령 이후에 volatile 변수 접근 후의 코드가 실행되도록 한다.
 *
 *
 *
 * 문제가 발생되지 않는 경우
 * 1. x = 1;
 * 2. y = x + 2;
 * 3. z = Y = 10;
 * 다만 위와 같은 상황일 경우에는 2번에서는 1번의 x를, 3번에서는 2번의 y를 참조해야 하기 때문에 Data Race 가 발생하지 않는다.
 */
public class Main {

    public static void main(String[] args) {
        SharedClass sharedClass = new SharedClass();

        Thread thread1 = new Thread(()->{
           for(int i = 0; i < Integer.MAX_VALUE; i++) {
               sharedClass.increase();
            }
        });

        Thread thread2 = new Thread(()->{
            for(int i = 0; i < Integer.MAX_VALUE; i++) {
                sharedClass.checkForDataRace();
            }
        });

        thread1.start();
        thread2.start();

    }

    public static class SharedClass {
        private volatile int x = 0;
        private volatile int y = 0;

        public void increase() {
            x++;
            y++;
        }

        public void checkForDataRace() {
            if(y > x) {
                System.out.println("y > x - Data Race is detected");
            }
        }
    }
}
