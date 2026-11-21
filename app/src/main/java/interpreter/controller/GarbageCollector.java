package interpreter.controller;

import interpreter.model.value.IValue;
import interpreter.model.value.RefValue;

import java.util.*;
import java.util.stream.Collectors;

public final class GarbageCollector {
    private GarbageCollector() {}

    //the addresses that occur directly in the RefValues of the SymTable
    public static Set<Integer> getAddrFromSymTable(Collection<IValue> symTableValues) {
        return symTableValues.stream()
                .filter(v -> v instanceof RefValue)
                .map(v -> ((RefValue) v).address())
                .collect(Collectors.toSet());
    }

    //UNSAFE: keeps only the heap entries referred to directly from the SymTable
    public static Map<Integer, IValue> unsafeGarbageCollector(Set<Integer> symTableAddr,
                                                              Map<Integer, IValue> heap) {
        return heap.entrySet().stream()
                .filter(e -> symTableAddr.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    //Computes all addresses reachable from roots directly or transitively through the heap.
    public static Set<Integer> reachableAddresses(Set<Integer> roots, Map<Integer, IValue> heap) {
        Set<Integer> reachable = new HashSet<>();
        Deque<Integer> worklist = new ArrayDeque<>();

        for (int addr : roots) {
            if (addr != 0 && reachable.add(addr)) {
                worklist.push(addr);
            }
        }

        while (!worklist.isEmpty()) {
            int addr = worklist.pop();
            IValue val = heap.get(addr);
            if (val instanceof RefValue rv && rv.address() != 0) {
                if (reachable.add(rv.address())) {
                    worklist.push(rv.address());
                }
            }
        }

        return reachable;
    }

    //SAFE: keeps all heap entries reachable from the SymTable, including through other heap entries
    public static Map<Integer, IValue> safeGarbageCollector(Set<Integer> symTableAddr,
                                                            Map<Integer, IValue> heap) {
        Set<Integer> reachable = reachableAddresses(symTableAddr, heap);
        return heap.entrySet().stream()
                .filter(e -> reachable.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}