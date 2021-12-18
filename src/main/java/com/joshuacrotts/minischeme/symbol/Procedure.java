package com.joshuacrotts.minischeme.symbol;

import com.joshuacrotts.minischeme.ast.MSSyntaxTree;

public class Procedure extends Symbol {

    /**
     *
     */
    private MSSyntaxTree procDef;

    public Procedure(MSSyntaxTree procDef) {
        super(SymbolType.SYMBOL_PROC);
        this.procDef = procDef;
    }

    public MSSyntaxTree getProcDef() {
        return this.procDef;
    }
}