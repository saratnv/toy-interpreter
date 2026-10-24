package interpreter.model.adt;

import interpreter.exceptions.ADTException;

import java.lang.foreign.AddressLayout;

//will be used for the symbol table
public interface IDictionary<K,V> {
    boolean isDefined(K key);
    V lookup(K key) throws ADTException;
    void put(K key, V value);
    void update(K key, V value) throws ADTException;

}
