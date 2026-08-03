package moffy.ticex.lib.context;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayDeque;
import java.util.Deque;

@SuppressWarnings("resource")
public class ContextStack<T> {

    private final Deque<ContextFrame<T>> localDeque;

    public ContextStack() {
        this.localDeque = new ArrayDeque<>();
    }

    public T get(){
        return getOrElse(null);
    }

    public T getOrElse(T other) {
        ContextFrame<T> local = localDeque.peek();
        if(local != null) {
            return local.get();
        }
        return other;
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
