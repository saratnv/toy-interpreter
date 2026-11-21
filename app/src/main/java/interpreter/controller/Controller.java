package interpreter.controller;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.stmt.IStmt;
import interpreter.repository.IRepository;
import interpreter.model.adt.IStack;
import interpreter.model.value.IValue;
import interpreter.model.value.RefValue;
import java.util.*;
import java.util.stream.Collectors;

import java.sql.Statement;
import java.util.Stack;

public class Controller {
    private final IRepository repository;
    private final boolean displayFlag;

    public Controller(IRepository repo, boolean displayFlag) {
        this.repository = repo;
        this.displayFlag = displayFlag;
    }

    public PrgState oneStep(PrgState state) throws MyException {
        IStack<IStmt> stack = state.getExeStack();
        if(stack.isEmpty()){
            throw new StatementException("program state stack is empty");
        }
        IStmt crtStmt = stack.pop();
        return crtStmt.execute(state);
    }

    public void allStep() throws MyException {
        PrgState prg = repository.getCrtPrg();
        repository.logPrgStateExec();
        display(prg);
        while(!prg.getExeStack().isEmpty()){
            oneStep(prg);
            prg.getHeap().setContent(
                    safeGarbageCollector(
                            getReachableAddress(prg.getSymTable().toMap().values(), prg.getHeap().getContent()),
                            prg.getHeap().getContent()
                    )
            );
            repository.logPrgStateExec();
        }
    }

    private void display(PrgState state){
        if(displayFlag){
            System.out.println(state.toString());
        }
    }

    private Set<Integer> getReachableAddress(Collection<IValue> symTableValues, Map<Integer, IValue> heap){
        Set<Integer> reachable = new HashSet<>();
        Deque<Integer> worklist = new ArrayDeque<>();

        for (IValue val : symTableValues) {
            if (val instanceof RefValue rv && rv.address() != 0) {
                if (reachable.add(rv.address())) {
                    worklist.push(rv.address());
                }
            }
        }

        while (!worklist.isEmpty()){
            int addr = worklist.pop();
            IValue heapVal = heap.get(addr);
            if (heapVal instanceof RefValue rv && rv.address() != 0) {
                if (reachable.add(rv.address())) {
                    worklist.push(rv.address());
                }
            }
        }

        return reachable;
    }

    private Map<Integer, IValue> safeGarbageCollector(Set<Integer> reachableAddresses, Map<Integer, IValue> heap){
        return heap.entrySet().stream()
                .filter(e -> reachableAddresses.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
