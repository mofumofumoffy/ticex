package moffy.ticex.lib.context;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayDeque;
import java.util.Deque;

@SuppressWarnings("resource")
public class ContextStack<T> {

    private final Deque<ContextFrame<T>> localDeque;
    private final T defaultValue;

    public ContextStack() {
        this(null);
    }

    public ContextStack(T defaultValue) {
        this.localDeque = new ArrayDeque<>();
        this.defaultValue = defaultValue;
    }

    public T get() {
        ContextFrame<T> local = localDeque.peek();
        if(local != null) {
            return local.get();
        }
        return defaultValue;
    }

    @ApiStatus.Internal
    public void close(ContextFrame<T> local) {
        if(localDeque.peek() == local) {
            localDeque.pop();
        }
    }

    public ContextFrame<T> open(T object) {
        ContextFrame<T> local = new ContextFrame<>(this, object);
        localDeque.push(local);
        return local;
    }
}
