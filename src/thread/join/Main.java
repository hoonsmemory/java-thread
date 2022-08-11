package thread.join;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * join() 메서드는 스레드가 종료될 때 까지 기다린 후 결과를 반환한다.
 * join(1000) 메서드 안에 시간을 부여했을 때 시간안에 종료가 안 되었을 경우 다음 로직이 실행되며, 종료가 되지 않은 스레드는 종료가 될 때 까지 계속 실행이 된다.
 * 만약, 모든 로직이 종료되었음에도 계속 실행 중인 스레드를 종료하고 싶다면 thread.setDaemon(true) 옵션을 주어 데몬 스레드로 만들면 된다.
 * JVM은 데몬 스레드의 종료를 기다리지 않고 강제 종료시켜 버리기 때문에 기다리고 싶지 않다면 데몬 스레드로 만들면 된다.
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        List<Long> inputNumbers = Arrays.asList(0L, 3435L, 35435L, 2324L, 4656L, 23L, 5556L, 100000L);
        List<FactorialThread> threads = new ArrayList<>();

        for (Long inputNumber : inputNumbers) {
            threads.add(new FactorialThread(inputNumber));
        }

        for (FactorialThread thread : threads) {
            thread.setDaemon(true);
            thread.start();
        }

        for(FactorialThread thread : threads) {
            thread.join(1000);
        }

        for(int i = 0; i < inputNumbers.size(); i++) {
            FactorialThread factorialThread = threads.get(i);

            if(factorialThread.isFinished()) {
                System.out.println("Factorial of " + inputNumbers.get(i) + " is " + factorialThread.getResult());
            } else {
                System.out.println("The Calculation for "  + inputNumbers.get(i) + " is still in progress");
            }
        }
    }

    public static class FactorialThread extends Thread {

        private long inputNumber;
        private BigInteger result = BigInteger.ZERO;
        private boolean isFinished = false;

        public FactorialThread(long inputNumber) {
            this.inputNumber = inputNumber;
        }

        @Override
        public void run() {
            this.result = factorial(inputNumber);
            this.isFinished = true;
        }

        private BigInteger factorial(long inputNumber) {
            BigInteger tempResult = BigInteger.ONE;

            for(long i = inputNumber; i > 0; i--) {
                tempResult = tempResult.multiply(new BigInteger(Long.toString(i)));
            }

            return tempResult;
        }

        public BigInteger getResult() {
            return result;
        }

        public boolean isFinished() {
            return isFinished;
        }
    }

}
