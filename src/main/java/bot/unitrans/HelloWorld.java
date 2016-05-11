package bot.unitrans;

import org.joda.time.LocalTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@SpringBootApplication
public class HelloWorld {
    private static final Object lock = new Object();
    private static Integer count = 0;

    public static void main(String[] args) throws InterruptedException {
        //http://winterbe.com/posts/2015/04/07/java8-concurrency-tutorial-thread-executor-examples/

        // Runnable + Thread
        Integer i = 0;
        Runnable task = () -> {
            try {
                String tName = Thread.currentThread().getName();
                System.out.println("The current thread is: " + tName);

                TimeUnit.SECONDS.sleep(1);
                synchronized (lock){
                    count++;
                    System.out.println("The current thread "+tName+" time is: " + new LocalTime() + "count: " + count.toString());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        task.run();
        Thread t = new Thread(task);
        t.start();
        Thread t0 = new Thread(task);
        t0.start();
        System.out.println("The current local time is: " + new LocalTime());

        // Executor + Runnable
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {

            String tName = Thread.currentThread().getName();
            System.out.println("Exec The current thread is: " + tName);
            try {
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException e) {
                //e.printStackTrace();
                System.out.println("Someone is killing "+tName);
            }
            System.out.println("Exec thread done: " + tName);
        });
        try{
            System.out.println("gracefully shut down thread executor");
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        }finally {
            if(!executor.isTerminated()){
                System.err.println("cancel non-finished tasks, kill! I can't for so long!");
                executor.shutdownNow();
            }
        }

        // Executor + Callable -> Future
        Callable<Integer> callable = () -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                return 123;
            } catch (InterruptedException e){
                throw new IllegalStateException("interrupted :(", e);
            }
        };
        ExecutorService executor1 = Executors.newFixedThreadPool(1);
        Future<Integer> f1 = executor1.submit(callable);
        System.out.println("future done? " + f1.isDone()+" " + new LocalTime());

        Integer futureInt1 = null;
        try {
            futureInt1 = f1.get();
        } catch (Exception e) {
            System.out.println("Unable to get from future :( ... err:" + e.getMessage());
        }
        System.out.println("future done? " + f1.isDone()+" " + new LocalTime());
        System.out.println(futureInt1);
        executor1.shutdown();

        // Executor/Callable/Future/Timeout
        ExecutorService executor2 = Executors.newFixedThreadPool(1);
        Future<Integer> f2 = executor2.submit(callable);
        Integer futureInt2 = null;
        try {
            futureInt2 = f2.get(1, TimeUnit.SECONDS);
            System.out.println(futureInt2);
        } catch (Exception e) {
            System.out.println("Unable to get from future :( ... err:" + e);
        }
        executor2.shutdown();

        // Pool
        ExecutorService executor3 = Executors.newWorkStealingPool(); //All processors
        List<Callable<String>> callables = Arrays.asList(
                () -> "task1",
                () -> "task2",
                () -> "task3");
        executor3.invokeAll(callables).stream().map(future -> {
            try {
                return future.get();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }).forEach(System.out::println);

        //SpringApplication.run(HelloWorld.class, args);
    }
}
