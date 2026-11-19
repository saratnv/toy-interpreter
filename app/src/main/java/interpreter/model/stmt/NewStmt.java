package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.IExp;
import interpreter.model.type.IType;
import interpreter.model.value.IValue;
import interpreter.model.value.RefValue;

public record NewStmt (String varName, IExp exp) implements IStmt {

    @Override
    public PrgState execute(PrgState state) throws MyException {
        var symTable = state.getSymTable();
        var heap = state.getHeap();

        if (!symTable.isDefined(varName)) {
            throw new StatementException("Variable " + varName + " is not defined.");
        }

        IValue varVal =symTable.lookup(varName);
        if(!(varVal instanceof RefValue(int address, IType locationType))){
            throw new StatementException("Variable " + varName + " is not of RefType.");
        }

        IValue evaluated = exp.eval(symTable, heap);
        if(!evaluated.getType().equals(locationType)){
            throw new  StatementException("Type mismatch: cannot allocate " + evaluated.getType() + " into Ref(" + locationType + ").");
        }

        int newAddress = heap.allocate(evaluated);
        symTable.update(varName, new RefValue(newAddress, locationType));
        return null;
    }

    @Override
    public String toString(){
        return "new(" + varName + ", " + exp + ")";
    }
}
