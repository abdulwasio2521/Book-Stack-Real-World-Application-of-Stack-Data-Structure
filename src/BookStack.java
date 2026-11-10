/** Array-based stack of books (LIFO). Strict rules: only the top can be read, changed or removed. */
public class BookStack {
    public static final int CAPACITY = 10;
    private final Book[] data = new Book[CAPACITY];
    private int top = -1;
    public final String name;

    public BookStack(String name) {
        this.name = name;
    }

    public boolean isEmpty() { return top == -1; }
    public boolean isFull()  { return top == CAPACITY - 1; }
    public int size()        { return top + 1; }

    public void push(Book b) {
        if (isFull()) throw new IllegalStateException(name + " overflow! It already holds " + CAPACITY + " books. Pop one first.");
        data[++top] = b;
    }

    public Book pop() {
        if (isEmpty()) throw new IllegalStateException(name + " underflow! There are no books to pop.");
        Book b = data[top];
        data[top--] = null;
        return b;
    }

    public Book peek() {
        if (isEmpty()) throw new IllegalStateException(name + " is empty. Push a book to begin.");
        return data[top];
    }

    public Book updateTop(String title, String color, String size) {
        Book b = peek();
        b.edit(title, color, size);
        return b;
    }

    /** JSON array, bottom book first, top book last. */
    public String toJson() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i <= top; i++) {
            if (i > 0) sb.append(',');
            sb.append(data[i].toJson());
        }
        return sb.append(']').toString();
    }
}
