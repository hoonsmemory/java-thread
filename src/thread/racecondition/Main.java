package thread.racecondition;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * race condition 문제
 * 문제 1) InventoryCounter가 두 스레드 모두로 전달되는 공유된 객체라서 items 멤버 변수 또한 두 스레드에서 공유되고 액세스 가능해진다는 것
 * 문제 2) 두 스레드가 increment(items++) 메서드와 decrement(items--) 메서드를 호출해서 수행하는 작업이 동시에 실행되는 점(원자적 작업이 아니다.)
 *
 * 예시
 * 1. 스레드-1 : items = 0 읽기
 * 2. 스레드-1 : itmes = itmes + 1 연산
 * 3. 스레드-2 : items = 0 읽기
 * 4. 스레드-2 : itmes = itmes - 1 연산
 * 5. 스레드-1 : itmes = 1 쓰기
 * 6. 스레드-2 : items = -1 쓰기
 *
 * items는 0이 되어야 하지만 두 스레드가 동시에 작업하다보니 마지막 쓰기를 작업한 items = -1 이 되는 문제가 발생되었다.
 *
 * 해결 방법은 원자적(atomic) 명령어를 쓰는 것이다.
 * 1. synchronized 키워드
 *    하지만 synchronized 키워드를 사용할 경우 멀티 스레드 환경에서 마치 싱글 스레드처럼 동시적 작업을 하지 않는다.
 *    그 이유는 다른 스레드에서 synchronized 키워드가 붙은 메서드를 호출할 경우 자기 차례가 오기까지 blocking 상태로 있기 때문이다.
 *    synchronized 키워드는 오히려 컨텍스트 스위칭, 메모리 오버헤드 등 자원 낭비가 심할 수 있다.
 *
 * 2. atomic 키워드
 *    atomic 키워드를 적용한 변수는 blocking을 사용하는 synchronized 키워드와는 달리 non-blocking 하면서 원자성을 보장하여 동기화 문제를 해결한다.
 *    atomic 키워드는 CAS(Compare And Swap) 알고리즘을 사용한다.
 *    즉 메인 메모리에 있는 기존의 값과 현재 가지고 있는 기존의 값이 같을 경우에만 연산이 일어난 값으로 변경을 하기 때문이다.
 *
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        InventoryCounter inventoryCounter = new InventoryCounter();
        IncrementingThread incrementingThread = new IncrementingThread(inventoryCounter);
        DecrementingThread decrementingThread = new DecrementingThread(inventoryCounter);

        incrementingThread.start();
        decrementingThread.start();

        incrementingThread.join();
        decrementingThread.join();

        System.out.println("We currently have " + inventoryCounter.getItems());
    }

    public static class IncrementingThread extends Thread {
        private InventoryCounter inventoryCounter;

        public IncrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for(int i = 0; i < 10000; i++) {
                inventoryCounter.increment();
            }
        }
    }

    public static class DecrementingThread extends Thread {
        private InventoryCounter inventoryCounter;

        public DecrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for(int i = 0; i < 10000; i++) {
                inventoryCounter.decrement();
            }
        }
    }

    private static class InventoryCounter {

        private AtomicInteger items = new AtomicInteger(0);

        public void increment() {
            items.incrementAndGet();
        }

        public void decrement() {
            items.decrementAndGet();
        }

        public  int getItems() {
            return items.get();
        }
    }
}
