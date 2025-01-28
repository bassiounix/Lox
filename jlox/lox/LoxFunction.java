package jlox.lox;

import java.util.List;

class LoxFunction implements LoxCallable {
    private final Stmt.Function declaration;
    private final Environment closure;
    private final boolean isInitializer;
    private final boolean isStatic;
    private final boolean isGetter;

    LoxFunction(Stmt.Function declaration, Environment closure, boolean isInitializer, boolean isStatic) {
        this.isInitializer = isInitializer;
        this.declaration = declaration;
        this.closure = closure;
        this.isStatic = isStatic;
        this.isGetter = declaration.params == null;
    }

    LoxFunction bind(LoxInstance instance) {
        Environment environment = new Environment(closure);
        environment.define("this", instance);

        return new LoxFunction(declaration, environment, this.isInitializer, false);
    }

    public boolean isStatic() {
        return this.isStatic;
    }

    public boolean isGetter() {
        return this.isGetter;
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme() + ">";
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);

        if (!this.isGetter) {
            for (int i = 0; i < declaration.params.size(); i++) {
                environment.define(declaration.params.get(i).lexeme(), arguments.get(i));
            }
        }

        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            if (isInitializer) {
                return closure.getAt(0, "this");
            }

            return returnValue.value;
        }

        if (isInitializer) {
            return closure.getAt(0, "this");
        }

        return null;
    }
}
