package async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Calculate {

    public static void main(String [] argc) throws Exception {

        Calculate calculate = new Calculate();
        Future<String> stringFuture = calculate.calculateAsyncWithCancellation();
        System.out.println(stringFuture.get());

    }

    public Future<String> calculateAsync() throws InterruptedException {

        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        Executors.newCachedThreadPool().submit(()->{
            try {
                Thread.sleep(500);
            } catch (Exception ex) {

            }

            completableFuture.complete("Hello");

        });
        return completableFuture;
    }

    public Future<String> calculateAsyncWithCancellation() throws InterruptedException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        Executors.newCachedThreadPool().submit(() ->{
            try {
                Thread.sleep(500);
                completableFuture.cancel(false);
                //return null;
            } catch (Exception ex) {

            }
        });
        return completableFuture;
    }
}
