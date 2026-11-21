package interpreter.model.stmt;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.IExp;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;

public record WhileStmt(IExp cond, IStmt body) implements IStmt {
    @Override
    public PrgState execute(PrgState state) throws MyException {
        IValue val = cond.eval(state.getSymTable(), state.getHeap());
        if(!(val instanceof BoolValue(boolean b))){
            throw new StatementException("While condition is not a boolean.");
        }

        if (b){
            state.getExeStack().push(this);
            state.getExeStack().push(body);
        }

        return null;
    }

    @Override
    public String toString(){
        return "while(" + cond + ") " + body;
    }
}
