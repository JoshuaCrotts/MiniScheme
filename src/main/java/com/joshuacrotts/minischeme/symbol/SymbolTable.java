package com.joshuacrotts.minischeme.symbol;

import com.joshuacrotts.minischeme.ast.MSSyntaxTree;

import java.util.HashMap;

public class SymbolTable {

    private HashMap<String, Symbol> table;

    public SymbolTable() {
        this.table = new HashMap<>();
    }

    public void addVariable(String identifier, MSSyntaxTree value) {
        this.table.put(identifier, new Variable(value));
    }

    public void addProcedure(String identifier, MSSyntaxTree procDef) {
        this.table.put(identifier, new Procedure(procDef));
    }

    public Variable getVariable(String sym) {
        return this.isVariable(sym) ? (Variable) this.table.get(sym) : null;
    }

    public Procedure getProcedure(String sym) {
        return this.isProcedure(sym) ? (Procedure) this.table.get(sym) : null;
    }

    public boolean hasSymbol(String identifier) {
        return this.table.containsKey(identifier);
    }

    public boolean isVariable(String identifier) {
        return this.hasSymbol(identifier) && this.table.get(identifier).getType() == SymbolType.SYMBOL_VAR;
    }

    public boolean isProcedure(String identifier) {
        return this.hasSymbol(identifier) && this.table.get(identifier).getType() == SymbolType.SYMBOL_PROC;
    }
}