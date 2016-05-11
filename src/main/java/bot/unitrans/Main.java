package bot.unitrans;

import spark.Route;
import com.google.gson.Gson;

import java.util.concurrent.atomic.AtomicLong;

import static spark.Spark.*;

/**
 * Created by yury on 11/05/16.
 */
public class Main {
    private final AtomicLong counter = new AtomicLong();
    private static final String template = "Hello, %s!";
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        new Main();
    }

    Main(){
        int maxThreads = 4;
        threadPool(maxThreads);

        get("/greeting", greeting(), gson::toJson);

    }

    protected Route greeting() {
        return (req, res) -> {
            String name = req.queryParams("name");
            if(name == null) {
                name = "World";
            }
            return new Greeting(counter.incrementAndGet(), String.format(template, name));
        };
    }



    class Greeting {

        private final long id;
        private final String content;

        public Greeting(long id, String content) {
            this.id = id;
            this.content = content;
        }

        public long getId() {
            return id;
        }

        public String getContent() {
            return content;
        }
    }

}
