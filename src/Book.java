import java.util.Locale;
import java.util.Random;

/** One book: data only. Look-and-feel values (tilt, face, decoration) are random per book. */
public class Book {
    private static int nextId = 1;
    private static final Random R = new Random();
    private static final String[] NAMES = {"Moon Bunny Tales", "Tiny Tea Party", "Cloud Cookbook", "Sleepy Fox Diary",
            "Star Jam Stories", "Pocket Dragons", "Mochi Magic", "The Cat Cafe", "Berry Garden", "Little Whale Songs"};

    public final int id = nextId++;
    public String title, color, size;
    public double w, d, t;
    public final double yaw = (R.nextDouble() - .5) * .6, ox = (R.nextDouble() - .5) * .16, oz = (R.nextDouble() - .5) * .16;
    public final int face = R.nextInt(3), deco = R.nextInt(3);

    public Book(String title, String color, String size) {
        apply(title, color, size, true);
    }

    /** Update: new title, color and size (size changes the book's dimensions). */
    public void edit(String title, String color, String size) {
        apply(title, color, size, false);
    }

    private void apply(String title, String color, String size, boolean jitter) {
        this.title = (title == null || title.trim().isEmpty()) ? NAMES[R.nextInt(NAMES.length)] : title.trim();
        this.color = color;
        this.size = size;
        double[] base = size.equals("S") ? new double[]{1.1, 1.5, .16}
                : size.equals("L") ? new double[]{1.7, 2.3, .32} : new double[]{1.4, 1.9, .24};
        double j = jitter ? .92 + R.nextDouble() * .16 : 1;
        w = base[0] * j;
        d = base[1] * j;
        t = base[2];
    }

    private static String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public String toJson() {
        return String.format(Locale.US,
                "{\"id\":%d,\"title\":\"%s\",\"color\":\"%s\",\"sz\":\"%s\",\"w\":%.4f,\"d\":%.4f,\"t\":%.4f,"
                        + "\"yaw\":%.4f,\"ox\":%.4f,\"oz\":%.4f,\"face\":%d,\"deco\":%d}",
                id, esc(title), esc(color), size, w, d, t, yaw, ox, oz, face, deco);
    }
}
