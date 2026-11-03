package interpreter.model.adt;


import interpreter.exceptions.ADTException;

import java.util.ArrayList;
import java.util.List;

public class MyList<T> implements IList<T> {
    private final List<T> list  =  new ArrayList<T>();

    @Override
    public void add(T item){
        list.add(item);
    }

    @Override
    public T get(int index) throws ADTException{
        if(index< 0 || index >= list.size()){
            throw new ADTException("Index out of bounds");
        }
        return list.get(index);
    }

    @Override
    public int size(){
        return list.size();
    }

    @Override
    public String toString(){
        return list.toString();
    }

    @Override
    public java.util.List<T> toList(){
        return java.util.List.copyOf(list);
    }
}
