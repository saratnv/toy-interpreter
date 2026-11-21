package interpreter.view;

import interpreter.model.exp.*;
import interpreter.model.stmt.*;
import interpreter.model.type.*;
import interpreter.model.value.*;

public class Examples {
    private static IStmt seq(IStmt... stmts){
        IStmt res = stmts[stmts.length-1];
        for (int i = stmts.length-2; i >= 0; i--) {
            res = new CompStmt(stmts[i], res);
        }
        return res;
    }

    //ex1: int v; v=2; print(v)
    public static final IStmt EX1 = new CompStmt(
            new VarDeclStmt("v", new IntType()),
            new CompStmt(
                    new AssignStmt("v", new ValueExp(new IntValue(2))),
                    new PrintStmt(new VarExp("v"))
            )
    );

    //ex2: int a; int b; a=2+3*5; b=a+1; print(b)
    public static final IStmt EX2 = new CompStmt(
            new VarDeclStmt("a", new IntType()),
            new CompStmt(
                    new VarDeclStmt("b", new IntType()),
                    new CompStmt(
                            new AssignStmt("a",
                                    new ArithExp(ArithOp.ADD, new ValueExp(new IntValue(2)),
                                            new ArithExp(ArithOp.MUL, new ValueExp(new IntValue(3)), new ValueExp(new IntValue(5))))),
                            new CompStmt(
                                    new AssignStmt("b",
                                            new ArithExp(ArithOp.ADD, new VarExp("a"), new ValueExp(new IntValue(1)))),
                                    new PrintStmt(new VarExp("b"))
                            )
                    )
            )
    );

    //ex3: bool a; int v; a=true; (if a then v=2 else v=3); print(v)
    public static final IStmt EX3 = new CompStmt(
            new VarDeclStmt("a", new BoolType()),
            new CompStmt(
                    new VarDeclStmt("v", new IntType()),
                    new CompStmt(
                            new AssignStmt("a", new ValueExp(new BoolValue(true))),
                            new CompStmt(
                                    new IfStmt( new VarExp("a"),
                                            new AssignStmt("v", new ValueExp(new IntValue(2))),
                                            new AssignStmt("v", new ValueExp(new IntValue(3)))),
                                    new PrintStmt(new VarExp("v"))
                            )
                    )
            )
    );

    public static IStmt ex4() {
        // string varf; varf="test.in"; openRFile(varf); int varc;
        // readFile(varf,varc); print(varc); readFile(varf,varc); print(varc); closeRFile(varf)
        return new CompStmt(
                new VarDeclStmt("varf", new StringType()),
                new CompStmt(
                        new AssignStmt("varf", new ValueExp(new StringValue("C:\\\\UNI2\\\\map\\\\toy-interpreter\\\\test.in"))),
                        new CompStmt(
                                new OpenRFileStmt(new VarExp("varf")),
                                new CompStmt(
                                        new VarDeclStmt("varc", new IntType()),
                                        new CompStmt(
                                                new ReadFileStmt(new VarExp("varf"), "varc"),
                                                new CompStmt(
                                                        new PrintStmt(new VarExp("varc")),
                                                        new CompStmt(
                                                                new ReadFileStmt(new VarExp("varf"), "varc"),
                                                                new CompStmt(
                                                                        new PrintStmt(new VarExp("varc")),
                                                                        new CloseRFileStmt(new VarExp("varf"))
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    public static IStmt ex5() {
        // int a; int b; a=5; b=7; if (a<b) then print(a) else print(b)
        return new CompStmt(
                new VarDeclStmt("a", new IntType()),
                new CompStmt(
                        new VarDeclStmt("b", new IntType()),
                        new CompStmt(
                                new AssignStmt("a", new ValueExp(new IntValue(5))),
                                new CompStmt(
                                        new AssignStmt("b", new ValueExp(new IntValue(7))),
                                        new IfStmt(
                                                new RelationalExp(RelOp.LT, new VarExp("a"), new VarExp("b")),
                                                new PrintStmt(new VarExp("a")),
                                                new PrintStmt(new VarExp("b"))
                                        )
                                )
                        )
                )
        );
    }

    // Ex6: Heap Allocation
    // Ref int v; new(v, 20); Ref Ref int a; new(a, v); print(v); print(a)
    // Expected Out: (1, int), (2, Ref(int))
    public static IStmt ex6() {
        return seq(
                new VarDeclStmt("v", new RefType(new IntType())),
                new NewStmt("v", new ValueExp(new IntValue(20))),
                new VarDeclStmt("a", new RefType(new RefType(new IntType()))),
                new NewStmt("a", new VarExp("v")),
                new PrintStmt(new VarExp("v")),
                new PrintStmt(new VarExp("a"))
        );
    }

    // Ex7: Heap Reading
    // Ref int v; new(v, 20); Ref Ref int a; new(a, v); print(rH(v)); print(rH(rH(a)) + 5)
    // Expected Out: 20, 25
    public static IStmt ex7() {
        return seq(
                new VarDeclStmt("v", new RefType(new IntType())),
                new NewStmt("v", new ValueExp(new IntValue(20))),
                new VarDeclStmt("a", new RefType(new RefType(new IntType()))),
                new NewStmt("a", new VarExp("v")),
                new PrintStmt(new ReadHeapExp(new VarExp("v"))),
                new PrintStmt(new ArithExp(ArithOp.ADD,
                        new ReadHeapExp(new ReadHeapExp(new VarExp("a"))),
                        new ValueExp(new IntValue(5))))
        );


    }

    // Ex8: Heap Writing
    // Ref int v; new(v, 20); print(rH(v)); wH(v, 30); print(rH(v) + 5)
    // Expected Out: 20, 35
    public static IStmt ex8() {
        return seq(
                new VarDeclStmt("v", new RefType(new IntType())),
                new NewStmt("v", new ValueExp(new IntValue(20))),
                new PrintStmt(new ReadHeapExp(new VarExp("v"))),
                new WriteHeapStmt("v", new ValueExp(new IntValue(30))),
                new PrintStmt(new ArithExp(ArithOp.ADD,
                        new ReadHeapExp(new VarExp("v")),
                        new ValueExp(new IntValue(5))))
        );
    }

    // Ex9: Safe Garbage Collector (nested reference must keep address 1 alive!)
    // Ref int v; new(v, 20); Ref Ref int a; new(a, v); new(v, 30); print(rH(rH(a)))
    // Expected Out: 20
    public static IStmt ex9() {
        return seq(
                new VarDeclStmt("v", new RefType(new IntType())),
                new NewStmt("v", new ValueExp(new IntValue(20))),
                new VarDeclStmt("a", new RefType(new RefType(new IntType()))),
                new NewStmt("a", new VarExp("v")),
                new NewStmt("v", new ValueExp(new IntValue(30))),
                new PrintStmt(new ReadHeapExp(new ReadHeapExp(new VarExp("a"))))
        );
    }

    // Ex10: While Statement
    // int v; v = 4; (while (v > 0) print(v); v = v - 1); print(v)
    // Expected Out: 4, 3, 2, 1, 0
    public static IStmt ex10() {
        return seq(
                new VarDeclStmt("v", new IntType()),
                new AssignStmt("v", new ValueExp(new IntValue(4))),
                new WhileStmt(
                        new RelationalExp(RelOp.GT, new VarExp("v"), new ValueExp(new IntValue(0))),
                        seq(
                                new PrintStmt(new VarExp("v")),
                                new AssignStmt("v", new ArithExp(ArithOp.SUB, new VarExp("v"), new ValueExp(new IntValue(1))))
                        )
                ),
                new PrintStmt(new VarExp("v"))
        );
    }

}
