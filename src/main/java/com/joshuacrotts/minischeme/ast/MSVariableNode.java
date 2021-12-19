package com.joshuacrotts.minischeme.ast;

/**
 * Defines a Scheme variable. Variables have two components: the
 * identifier (i.e., the name of the variable), and the expression
 * which that variable represents.
 */
public class MSVariableNode extends MSSyntaxTree {

    public MSVariableNode(MSSyntaxTree identifier, MSSyntaxTree expr) {
        super(MSNodeType.MS_VAR);
        this.addChild(identifier);
        this.addChild(expr);
    }

    private MSVariableNode() {
        super(MSNodeType.MS_VAR);
    }

    @Override
    public MSSyntaxTree copy() {
        MSVariableNode varCopy = new MSVariableNode();
        for (MSSyntaxTree ch : this.getChildren()) {
            varCopy.addChild(ch.copy());
        }
        return varCopy;
    }

    public String getStringRep() {
        return this.getChild(0).getStringRep() + ": " + this.getChild(1).getStringRep();
    }

    public String toString() {
        return "(VAR " + this.getChild(0).toString() + ": " + this.getChild(1).toString() + ")";
    }
}
