package thread.racecondition;

/**
 * race condition 문제
 * 문제 1) InventoryCounter가 두 스레드 모두로 전달되는 공유된 객체라서 items 멤버 변수 또한 두 스레드에서 공유되고 액세스 가능해진다는 것
 * 문제 2) 두 스레드가 increment(items++) 메서드와 decrement(items--) 메서드를 호출해서 수행하는 작업이 동시에 실행되는 점(원자적 작업이 아니다.)
 *
 * CPU는 메모리에 적재되어 있는 값을 직접적으로 연산 하는것은 불가능하다.
 * 메모리에 적재되어 있는 값을 레지스터로 가져와서 연산을 수행하고 연산 결과를 레지스터에서 다시 메모리에 써야 한다.
 * 위의 과정은 읽기, 연산, 쓰기의 명령어가 각각 일어나게 된다.
 *
 * 예시
 * 1. 스레드-1 : items = 0 읽기
 * 2. 스레드-1 : itmes = itmes + 1 연산
 * 3. 스레드-2 : items = 0 읽기
 * 4. 스레드-2 : itmes = itmes - 1 연산
 * 5. 스레드-1 : itmes = 1 쓰기
 * 6. 스레드-2 : items = -1 쓰기
 *
 * items는 0이 되어야 하지만 두 스레드가 동시에 작업하다보니 이러한 문제가 발생되었다.
 *
 * 해결 방법은 원자적(atomic) 명령어를 쓰는 것이다.
 * 1. 자바에서는 synchronized 키워드를 쓰는 방법이 있다.
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

        private int items = 0;

        public void increment() {
            items++;
        }

        public void decrement() {
            items--;
        }

        public  int getItems() {
            return items;
        }
    }
}
