package thread.interrupt.exam2;

import java.math.BigInteger;

/**
 * for문 같이 루프가 돌고 있을 경우 interupt() 메서드가 동작되지 않는다.
 * 따라서 Thread.currentThread().isInterrupted() 메서드로 체크가 필요하다.
 *
 * interrupt() 메소드는 스레드가 일시 정지 상태에 있을 때 InterruptedException 예외를 발생시키는 역할을 한다.
 * 주목 할 점은 스레드가 실행 대기 또는 실행 상태에 있을 때 interrupt() 메소드가 실행되면 즉시 InterruptedException 예외가 발생하지 않고,
 * 스레드가 미래에 일시 정지 상태가 되면 즉시 InterruptedException 예외가 발생한다는 것이다.
 */
public class Main {

    public static void main(String[] args) {
        Thread thread = new Thread(new LongComputationTask(new BigInteger("200000"), new BigInteger("300000000")));
        thread.start();
        thread.interrupt();
    }

    private static class LongComputationTask implements Runnable {

        BigInteger base, power;

        public LongComputationTask(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
            System.out.println(base + "^" + power + " = " + pow(base, power));
        }

        private BigInteger pow(BigInteger base, BigInteger power) {

            BigInteger result = BigInteger.ONE;

            for(BigInteger i = BigInteger.valueOf(0); i.compareTo(power) != 0; i = i.add(BigInteger.ONE)) {

                //매번 for문이 돌때마다 interrupt가 되었는지 체크한다.
                if(Thread.currentThread().isInterrupted()) {
                    System.out.println("Prematurely interrupted computation");
                    return BigInteger.ZERO;
                }

                result = result.multiply(base);
            }

            return result;
        }
    }

}
