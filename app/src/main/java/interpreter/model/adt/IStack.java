package interpreter.model.adt;

import interpreter.exceptions.ADTException;

import java.util.EmptyStackException;

public interface IStack<T> {
    void push(T v);
    T pop() throws ADTException;
    boolean isEmpty();
}
