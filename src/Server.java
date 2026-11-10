import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/** Tiny HTTP server: serves web/index.html and the CRUD API. Run: java -cp out Server */
public class Server {
    static final BookStack[] STACKS = {new BookStack("Stack A"), new BookStack("Stack B"), new BookStack("Stack C")};

    public static void main(String[] args) throws Exception {
        STACKS[0].push(new Book("Moon Bunny Tales", "#8ecae6", "L"));
        STACKS[0].push(new Book("Tiny Tea Party", "#95e1a3", "M"));
        STACKS[0].push(new Book("Cloud Cookbook", "#ffa94d", "S"));
        STACKS[1].push(new Book("Sleepy Fox Diary", "#c5a3ff", "M"));
        STACKS[1].push(new Book("Star Jam Stories", "#ff8fb8", "S"));
        STACKS[2].push(new Book("Mochi Magic", "#7fdbd1", "L"));

        String env = System.getenv("PORT");
        int port = env == null ? 8080 : Integer.parseInt(env);   // hosting platforms provide PORT
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", ex -> {
            byte[] page = Files.readAllBytes(Paths.get("web", "index.html"));
            send(ex, 200, "text/html; charset=utf-8", page);
        });
        server.createContext("/api/", Server::api);
        server.start();
        System.out.println("Book stack running at http://localhost:" + port + " (press Ctrl+C to stop)");
        Thread.currentThread().join();   // keep the program alive until you stop it
    }

    static synchronized void api(HttpExchange ex) throws IOException {
        try {
            Map<String, String> q = query(ex.getRequestURI().getRawQuery());
            String path = ex.getRequestURI().getPath();
            if (path.equals("/api/stacks")) {
                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < STACKS.length; i++) sb.append(i > 0 ? "," : "").append(STACKS[i].toJson());
                json(ex, 200, sb.append(']').toString());
                return;
            }
            BookStack s = STACKS[Integer.parseInt(q.getOrDefault("stack", "0"))];
            String color = q.getOrDefault("color", "#ff8fb8"), size = q.getOrDefault("size", "M");
            switch (path) {
                case "/api/push": { Book b = new Book(q.get("title"), color, size); s.push(b); json(ex, 200, b.toJson()); break; }
                case "/api/pop":    json(ex, 200, s.pop().toJson()); break;
                case "/api/peek":   json(ex, 200, s.peek().toJson()); break;
                case "/api/update": json(ex, 200, s.updateTop(q.get("title"), color, size).toJson()); break;
                default:            json(ex, 404, "{\"error\":\"Unknown route\"}");
            }
        } catch (IllegalStateException e) {
            json(ex, 409, "{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");   // overflow / underflow
        } catch (RuntimeException e) {
            json(ex, 400, "{\"error\":\"Bad request\"}");
        }
    }

    static Map<String, String> query(String raw) {
        Map<String, String> m = new HashMap<>();
        if (raw == null) return m;
        for (String kv : raw.split("&")) {
            String[] p = kv.split("=", 2);
            m.put(URLDecoder.decode(p[0], StandardCharsets.UTF_8),
                  p.length > 1 ? URLDecoder.decode(p[1], StandardCharsets.UTF_8) : "");
        }
        return m;
    }

    static void json(HttpExchange ex, int code, String body) throws IOException {
        send(ex, code, "application/json; charset=utf-8", body.getBytes(StandardCharsets.UTF_8));
    }

    static void send(HttpExchange ex, int code, String type, byte[] body) throws IOException {
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(code, body.length);
        ex.getResponseBody().write(body);
        ex.close();
    }
}
