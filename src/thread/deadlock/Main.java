package thread.deadlock;

import java.util.Random;

/**
 * Dead Lock 문제
 *
 * 데드락이 걸리는 이유 - 아래 네 가지 조건이 충족되면 데드락이 걸린다.
 * 1) 상호 배제(Mutual Exclusion)
 *    한 번에 한 스레드만 단독으로 리소스에 액세스할 수 있다.
 *
 * 2) 점유와 대기(Hold and Wait)
 *    최소 하나의 스레드가 리소스를 점유하며 다른 리소스에 대기한다.
 *
 * 3) 비선점 할당(Non-preemptive Allocation)
 *    스레드가 사용 완료할 때까지 리소스를 사용할 수 없다.
 *    다른 스레드의 리소스를 뺏을 수 없고 해당 스레드의 리소스 사용이 끝날 때까지 기다려야 한다.
 *
 * 4) 순환 대기(Circular Wait)
 *    한 스레드가 철도 A를 점유하며 다른 스레드가 점유한 철도 B를 기다리고, 철도 B를 점유한 스레드는 철도 A를 기다리는 상황
 *
 * 해결 방법
 * 데드락을 예방하는 가장 간단한 방법은 마지막 조건인 순환 대기를 예방하는 것이다.
 * 동일한 순서로 공유 리소스를 잠그고 모든 코드에 해당 순서를 유지하면 된다. (락킹 순서 유지)
 */
public class Main  {

    public static void main(String[] args) {

        Intersection intersection = new Intersection();
        Thread trainAThread = new Thread(new TrainA(intersection));
        Thread trainBThread = new Thread(new TrainB(intersection));

        trainAThread.start();
        trainBThread.start();
    }

    public static class TrainA implements Runnable {
        private Intersection intersection;
        private Random random = new Random();

        public TrainA(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while (true) {
                long sleepingTime = random.nextInt(5);

                try {
                    //기차가 지나가는 시간
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                intersection.takeRoadA();
            }

        }
    }

    public static class TrainB implements Runnable {
        private Intersection intersection;
        private Random random = new Random();

        public TrainB(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while (true) {
                long sleepingTime = random.nextInt(5);

                try {
                    //기차가 지나가는 시간
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                intersection.takeRoadB();
            }

        }
    }


    public static class Intersection {

        private Object roadA = new Object();
        private Object roadB = new Object();

        public void takeRoadA() {
            synchronized (roadA) {
                System.out.println("Road A is locked by thread " + Thread.currentThread().getName());

                synchronized (roadB) {
                    System.out.println("Train is passing through road A");

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        public void takeRoadB() {
            synchronized (roadA) {
                System.out.println("Road B is locked by thread " + Thread.currentThread().getName());

                synchronized (roadB) {
                    System.out.println("Train is passing through road A");

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }


    }
}
