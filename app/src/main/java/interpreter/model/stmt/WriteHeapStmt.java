package interpreter.model.stmt;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.IExp;
import interpreter.model.type.IType;
import interpreter.model.value.IValue;
import interpreter.model.value.RefValue;

public record WriteHeapStmt(String varName, IExp exp) implements IStmt {
    @Override
    public PrgState execute(PrgState prgState) throws MyException {
        var symTable = prgState.getSymTable();
        var heap = prgState.getHeap();

        if(!symTable.isDefined(varName)){
            throw new StatementException("wH: variable '" + varName + "' is not defined.");
        }

        IValue varVal = symTable.lookup(varName);
        if(!(varVal instanceof RefValue(int address, IType locationType))){
            throw new StatementException("wH: variable '" + varName + "' is not of RefType.");
        }

        if (!heap.isDefined(address)){
            throw new StatementException("wH: address " + address + " is not defined in the heap.");
        }

        IValue evaluated = exp.eval(symTable, heap);
        if (!evaluated.getType().equals(locationType)){
            throw new StatementException("wH: type mismatch, expected " + locationType + " but got " + evaluated.getType() + ".");
        }

        heap.update(address, evaluated);
        return null;
    }

    @Override
    public String toString() {
        return "wH(" + varName + ", " + exp + ")";
    }
}
