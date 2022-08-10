package thread.throughput;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * JMeter 프로그램을 이용하여 Throughput을 측정
 * 스레드의 개수를 1개부터 점점 증가하여 성능 테스트를 진행한다.
 *
 * 스레드가 더 많다는 것은 더 많은 요청을 처리할 수 있다는 뜻이지만,
 * 오버헤드와 컨텍스트 스위칭도 더 많아지기 때문에 최적의 스레드 개수를 미리 알 수 있는 방법은 없다.
 */
public class ThroughputHttpServer {
    private static final String INPUT_FILE = "resource/throughput/war_and_peace.txt";
    private static final int NUMBER_OF_THREAD = 8;

    public static void main(String[] args) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get(INPUT_FILE)));
        startServer(text);
    }

    public static void startServer(String text) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/search", new WordCountHandler(text));

        Executor executor = Executors.newFixedThreadPool(NUMBER_OF_THREAD);
        server.setExecutor(executor);
        server.start();
    }

    private static class WordCountHandler implements HttpHandler {

        private String text;

        public WordCountHandler(String text) {
            this.text = text;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {

            String query = exchange.getRequestURI().getQuery();
            String[] keyValue = query.split("=");
            String action = keyValue[0];
            String word = keyValue[1];

            if(!action.equals("word")) {
                exchange.sendResponseHeaders(400, 0);
                return;
            }

            long count = countWord(word);

            byte[] response = Long.toString(count).getBytes();
            exchange.sendResponseHeaders(200, response.length);
            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response);
            outputStream.close();
        }

        private long countWord(String word) {
            long count = 0;
            int index = 0;

            while (index >= 0 ) {
                index = text.indexOf(word, index);

                if( index >= 0 ) {
                    count++;
                    index++;
                }
            }

            return count;
        }
    }
}
