package interpreter.model.stmt;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.IExp;
import interpreter.model.type.IntType;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;

import java.io.BufferedReader;
import java.io.IOException;

public record ReadFileStmt(IExp exp, String varName) implements IStmt {
    @Override
    public PrgState execute(PrgState state) throws MyException {
        var symTable = state.getSymTable();

        if (!symTable.isDefined(varName)) {
            throw new StatementException("readFile: variable '" + varName + "' is not declared");
        }
        if (!symTable.lookup(varName).getType().equals(new IntType())) {
            throw new StatementException("readFile: variable '" + varName + "' is not of type int");
        }

        if (!(exp.eval(symTable) instanceof StringValue fileName)) {
            throw new StatementException("readFile: " + exp + " is not a string");
        }

        var fileTable = state.getFileTable();
        if (!fileTable.isDefined(fileName)) {
            throw new StatementException("readFile: file" + fileName + " is not in the fileTable");
        }

        try{
            BufferedReader reader = fileTable.lookup(fileName);
            String line = reader.readLine();

            int number = 0;
            if (line != null && !line.trim().isEmpty()) {
                number = Integer.parseInt(line.trim());
            }

            symTable.update(varName, new IntValue(number));
        } catch (IOException e) {
            throw new StatementException("readFile: I/O error reading from " + fileName + ": " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new StatementException("readFile: invalid integer format in " + fileName + ": " + e.getMessage());
        }

        return state;
    }

    @Override
    public String toString(){
        return "readFile(" + exp + ", " + varName + ")";
    }
}
