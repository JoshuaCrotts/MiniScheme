package com.joshuacrotts.minischeme.main;

import com.joshuacrotts.minischeme.ast.MSSyntaxTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class Environment {

    /**
     *
     */
    private Stack<SymbolTable> environments;

    public Environment() {
        this.environments = new Stack<>();
    }

    public void addEnvironment() {
        this.environments.push(new SymbolTable());
    }

    public void addVariable(String identifier, MSSyntaxTree value) {
        this.environments.peek().addVariable(identifier, value);
    }

    public void addProcedure(String identifier, MSSyntaxTree ... body) {
        this.environments.peek().addProcedure(identifier, new ArrayList<MSSyntaxTree>(Arrays.asList(body)));
    }

    public void addProcedure(String identifier, ArrayList<MSSyntaxTree> body) {
        this.environments.peek().addProcedure(identifier, body);
    }

    public boolean hasSymbol(String identifier) {
        return this.environments.peek().hasSymbol(identifier);
    }
}
