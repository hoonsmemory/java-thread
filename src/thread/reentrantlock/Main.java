package thread.reentrantlock;

import javafx.animation.AnimationTimer;
import javafx.animation.FillTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock
 * 객체에 적용된 synchronized 키워드처럼 작동하며 명시적으로 lock 지점과 unlock 지점을 설정할 수 있다.
 * 또한 ReentrantLock은 더 많은 제어 기능과 고급 기능을 제공한다.
 *
 * 1. new ReentrantLock(true)
 * ReentrantLock이 빛을 발휘할 때는 락의 공정성을 제어할 때다.
 * 하지만 공정성을 유지하면 문제가 생긴다.
 * 락을 얻으려면 시간이 오래 걸려서 앱의 처리량이 줄어들 수 있다.
 * 그래서 공정성 플래그는 꼭 필요할 때만 사용해야 한다.
 *
 * 2. getQueuedThreads()
 * 락을 기다리는 스레드 목록을 반환한다.
 *
 * 3. getOwner()
 * 락을 가지고 있는 스레드를 반환한다.
 *
 * 4. isHeldByCurrentThread()
 * 현재 스레드에 락이 있으면 참을 반환한다.
 *
 * 5. isLocked()
 * 스레드에 락이 있는지 없는지 알려준다.
 *
 * 6. lock(), unlock()
 * lock을 얻고 다른 스레드가 임계 영역에 들어가지 못하게 된다. 그리고나서 공유 리소스를 안전하게 사용할 수 있도록 한다.
 * 락이 걸린 작업이 완료가 되었으면 unlock()을 이용해서 lock을 반환하여 다른 스레드가 공유 리소스를 사용할 수 있도록 한다.
 *
 * 특정 스레드가 lock을 얻으려고 할 때 이미 다른 스레드에 lock을 소유하고 있다면 그 스레드는 lock을 얻기 전까지 blocking하게 된다.
 * 이 경우에는 thread.interrupt() 메서드를 호출해도 소용없다.
 *
 * 또한 lock() 메서드를 통해 lock을 얻은 상황에서 오류가 발생되면 lock 객체를 반환할 수 없게 되고 큰 문제가 발생한다.
 * 따라서 try-catch 블럭을 이용해서 finally 영역에서 unlock() 메서드를 반드시 설정해야 한다.
 *
 * 7. lockInterruptibly()
 * 다른 스레드가 이미 lock을 가지고 있고 lock() 메서드가 아닌 lockInterruptibly() 메서드를 호출하면 중단 상태에서 벗어날 수 있다.
 *
 * 8. tryLock()
 * tryLock() 메서드를 사용하면 lock() 메서드처럼 lock을 얻는다.
 * 만약 lock을 점유할 수 있다면 tryLock() 메서드는 lock을 얻고 true를 반환한다.
 * 그런데 다른 스레드가 이미 lock을 점유했다면 스레드를 blocking 하는 대신에 false를 반환하고 다음 명령으로 넘어갔다 다시 lock 객체를 얻기를 시도한다.
 */
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cryptocurrency Prices");

        GridPane grid = createGrid();
        Map<String, Label> cryptoLabels = createCryptoPriceLabels();

        addLabelsToGrid(cryptoLabels, grid);

        double width = 300;
        double height = 250;

        StackPane root = new StackPane();

        Rectangle background = createBackgroundRectangleWithAnimation(width, height);

        root.getChildren().add(background);
        root.getChildren().add(grid);

        primaryStage.setScene(new Scene(root, width, height));

        PricesContainer pricesContainer = new PricesContainer();

        PriceUpdater priceUpdater = new PriceUpdater(pricesContainer);

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (pricesContainer.getLockObject().tryLock()) {
                    try {
                        Label bitcoinLabel = cryptoLabels.get("BTC");
                        bitcoinLabel.setText(String.valueOf(pricesContainer.getBitcoinPrice()));

                        Label etherLabel = cryptoLabels.get("ETH");
                        etherLabel.setText(String.valueOf(pricesContainer.getEtherPrice()));

                        Label litecoinLabel = cryptoLabels.get("LTC");
                        litecoinLabel.setText(String.valueOf(pricesContainer.getLitecoinPrice()));

                        Label bitcoinCashLabel = cryptoLabels.get("BCH");
                        bitcoinCashLabel.setText(String.valueOf(pricesContainer.getBitcoinCashPrice()));

                        Label rippleLabel = cryptoLabels.get("XRP");
                        rippleLabel.setText(String.valueOf(pricesContainer.getRipplePrice()));
                    } finally {
                        pricesContainer.getLockObject().unlock();
                    }
                }
            }
        };

        addWindowResizeListener(primaryStage, background);

        animationTimer.start();

        priceUpdater.start();

        primaryStage.show();
    }

    private void addWindowResizeListener(Stage stage, Rectangle background) {
        ChangeListener<Number> stageSizeListener = ((observable, oldValue, newValue) -> {
            background.setHeight(stage.getHeight());
            background.setWidth(stage.getWidth());
        });
        stage.widthProperty().addListener(stageSizeListener);
        stage.heightProperty().addListener(stageSizeListener);
    }

    private Map<String, Label> createCryptoPriceLabels() {
        Label bitcoinPrice = new Label("0");
        bitcoinPrice.setId("BTC");

        Label etherPrice = new Label("0");
        etherPrice.setId("ETH");

        Label liteCoinPrice = new Label("0");
        liteCoinPrice.setId("LTC");

        Label bitcoinCashPrice = new Label("0");
        bitcoinCashPrice.setId("BCH");

        Label ripplePrice = new Label("0");
        ripplePrice.setId("XRP");

        Map<String, Label> cryptoLabelsMap = new HashMap<>();
        cryptoLabelsMap.put("BTC", bitcoinPrice);
        cryptoLabelsMap.put("ETH", etherPrice);
        cryptoLabelsMap.put("LTC", liteCoinPrice);
        cryptoLabelsMap.put("BCH", bitcoinCashPrice);
        cryptoLabelsMap.put("XRP", ripplePrice);

        return cryptoLabelsMap;
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        return grid;
    }

    private void addLabelsToGrid(Map<String, Label> labels, GridPane grid) {
        int row = 0;
        for (Map.Entry<String, Label> entry : labels.entrySet()) {
            String cryptoName = entry.getKey();
            Label nameLabel = new Label(cryptoName);
            nameLabel.setTextFill(Color.BLUE);
            nameLabel.setOnMousePressed(event -> nameLabel.setTextFill(Color.RED));
            nameLabel.setOnMouseReleased((EventHandler) event -> nameLabel.setTextFill(Color.BLUE));

            grid.add(nameLabel, 0, row);
            grid.add(entry.getValue(), 1, row);

            row++;
        }
    }

    private Rectangle createBackgroundRectangleWithAnimation(double width, double height) {
        Rectangle backround = new Rectangle(width, height);
        FillTransition fillTransition = new FillTransition(Duration.millis(1000), backround, Color.LIGHTGREEN, Color.LIGHTBLUE);
        fillTransition.setCycleCount(Timeline.INDEFINITE);
        fillTransition.setAutoReverse(true);
        fillTransition.play();
        return backround;
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public static class PricesContainer {
        private Lock lockObject = new ReentrantLock();

        private double bitcoinPrice;
        private double etherPrice;
        private double litecoinPrice;
        private double bitcoinCashPrice;
        private double ripplePrice;

        public Lock getLockObject() {
            return lockObject;
        }

        public double getBitcoinPrice() {
            return bitcoinPrice;
        }

        public void setBitcoinPrice(double bitcoinPrice) {
            this.bitcoinPrice = bitcoinPrice;
        }

        public double getEtherPrice() {
            return etherPrice;
        }

        public void setEtherPrice(double etherPrice) {
            this.etherPrice = etherPrice;
        }

        public double getLitecoinPrice() {
            return litecoinPrice;
        }

        public void setLitecoinPrice(double litecoinPrice) {
            this.litecoinPrice = litecoinPrice;
        }

        public double getBitcoinCashPrice() {
            return bitcoinCashPrice;
        }

        public void setBitcoinCashPrice(double bitcoinCashPrice) {
            this.bitcoinCashPrice = bitcoinCashPrice;
        }

        public double getRipplePrice() {
            return ripplePrice;
        }

        public void setRipplePrice(double ripplePrice) {
            this.ripplePrice = ripplePrice;
        }
    }

    public static class PriceUpdater extends Thread {
        private PricesContainer pricesContainer;
        private Random random = new Random();

        public PriceUpdater(PricesContainer pricesContainer) {
            this.pricesContainer = pricesContainer;
        }

        @Override
        public void run() {
            while (true) {
                pricesContainer.getLockObject().lock();

                try {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    pricesContainer.setBitcoinPrice(random.nextInt(20000));
                    pricesContainer.setEtherPrice(random.nextInt(2000));
                    pricesContainer.setLitecoinPrice(random.nextInt(500));
                    pricesContainer.setBitcoinCashPrice(random.nextInt(5000));
                    pricesContainer.setRipplePrice(random.nextDouble());
                } finally {
                    pricesContainer.getLockObject().unlock();
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
