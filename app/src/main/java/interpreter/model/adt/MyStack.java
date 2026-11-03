package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import java.util.ArrayDeque;
import java.util.Deque;

import java.util.Stack;

public class MyStack<T> implements IStack<T> {
    private final Deque<T> stack = new ArrayDeque<>();

    @Override
    public void push(T v) {
        stack.push(v);
    }

    @Override
    public T pop() throws ADTException{
        if(stack.isEmpty()){
            throw new ADTException("Pop from an empty stack");
        }
        return stack.pop();
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public String toString(){
        return stack.toString();
    }

    @Override
    public java.util.List<T> toList(){
        java.util.List<T> elements = new java.util.ArrayList<>(stack);
        java.util.Collections.reverse(elements);
        return elements;
    }
}
