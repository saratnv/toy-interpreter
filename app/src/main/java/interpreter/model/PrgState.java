package interpreter.model;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.IList;
import interpreter.model.adt.IStack;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.model.stmt.IStmt;
import interpreter.model.value.IValue;
import interpreter.model.value.StringValue;

import java.io.BufferedReader;

public class PrgState {
    private IStack<IStmt> exeStack;
    private IDictionary<String, IValue> symTable;
    private IList<IValue> out;
    private IDictionary<StringValue, BufferedReader> fileTable;
    private final IStmt originalProgram;

    public PrgState(IStack<IStmt> exeStack, IDictionary<String, IValue> symTable, IList<IValue> out,IDictionary<StringValue, BufferedReader> fileTable, IStmt program){
        this.exeStack = exeStack;//instruction pointer queue, holds statements
        this.symTable = symTable;//variable memory, var names+their values
        this.out = out; //output stream
        this.fileTable = fileTable;
        this.originalProgram = program;
        if (program != null) {
            this.exeStack.push(program);
        }
    }

    public static PrgState of(IStmt program) {
        return new PrgState(new MyStack<>(), new MyDictionary<>(), new MyList<>(),
                new MyDictionary<>(), program);
    }

    public IStack<IStmt> getExeStack(){ return exeStack; }
    public void setExeStack(IStack<IStmt> exeStack){this.exeStack = exeStack; }

    public IDictionary<String, IValue> getSymTable() { return symTable; }
    public void setSymTable(IDictionary<String, IValue> symTable) { this.symTable = symTable; }

    public IList<IValue> getOut() { return out; }
    public void setOut(IList<IValue> out) { this.out = out; }

    public IStmt getOriginalProgram() { return originalProgram; }

    public IDictionary<StringValue, BufferedReader> getFileTable() {
        return fileTable;
    }

    @Override
    public String toString(){
        return "ExeStack: " + exeStack.toString() + "\n" +
                "SymTable: " + symTable.toString() + "\n" +
                "Out: " + out.toString() + "\n";
    }
}
