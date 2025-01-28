package lox;

import java.util.List;
import java.util.Map;

class LoxClass extends LoxInstance implements LoxCallable {
    final String name;
    private final Map<String, LoxFunction> methods;

    LoxClass(String name, Map<String, LoxFunction> methods) {
        super(null);
        this.name = name;
        this.methods = methods;
        super.setKlass(this);
    }

    LoxFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            if (!methods.get(name).isStatic()) {
                return methods.get(name);
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        LoxInstance instance = new LoxInstance(this);
        LoxFunction initializer = findMethod("init");
        if (initializer != null) {
            initializer.bind(instance).call(interpreter, arguments);
        }

        return instance;
    }

    @Override
    public int arity() {
        LoxFunction initializer = findMethod("init");
        if (initializer == null) {
            return 0;
        }

        return initializer.arity();
    }

    @Override
    public Object get(Token name) {
        if (!methods.containsKey(name.lexeme())) {
            throw new RuntimeError(name, "Undefined property '" + name.lexeme() + "'.");
        }

        LoxFunction fn = methods.get(name.lexeme());

        if (!fn.isStatic()) {
            throw new RuntimeError(name, "access of non-static methods from a class isn't allowed");
        }

        return fn;
    }
}
