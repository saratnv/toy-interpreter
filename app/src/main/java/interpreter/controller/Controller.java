package interpreter.controller;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.stmt.IStmt;
import interpreter.repository.IRepository;
import interpreter.model.adt.IStack;

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
        display(prg);
        while(!prg.getExeStack().isEmpty()){
            oneStep(prg);
            display(prg);
        }
    }

    private void display(PrgState state){
        if(displayFlag){
            System.out.println(state.toString());
        }
    }
}
