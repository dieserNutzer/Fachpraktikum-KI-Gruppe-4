package massim.javaagents.massimworld.util;

/**
 * Provides a pair of objects with first and second element.
 * @param <T> type of the first element
 * @param <U> type of the second element
 */
public class Pair<T,U> {
    public T first;
    public U second;

    private Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public static <T,U> Pair<T,U> of(T first, U second) {
        return new Pair<T,U>(first, second);
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public U getSecond() {
        return second;
    }

    public void setSecond(U second) {
        this.second = second;
    }
}
