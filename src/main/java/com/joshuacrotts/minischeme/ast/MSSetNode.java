package com.joshuacrotts.minischeme.ast;

import com.joshuacrotts.minischeme.MiniSchemeParser;

/**
 *
 *
 * @author Joshua Crotts
 * @version 12/23/2021
 */
public class MSSetNode extends MSSyntaxTree {

    /**
     *
     */
    private int opType;

    public MSSetNode(int opType, MSSyntaxTree identifierNode, MSSyntaxTree exprNode) {
        super(MSNodeType.SET, identifierNode, exprNode);
        this.opType = opType;
    }

    @Override
    public MSSyntaxTree copy() {
        return null;
    }

    @Override
    public String getStringRep() {
        return this.getSetOpTypeString(this.opType);
    }

    @Override
    public String toString() {
        return this.getNodeType().toString();
    }

    private String getSetOpTypeString(int opType) {
        String literalName = MiniSchemeParser.VOCABULARY.getLiteralName(opType);
        return literalName.substring(1, literalName.length() - 1);
    }

    public int getOpType() {
        return this.opType;
    }

    public MSSyntaxTree getIdentifier() {
        return this.getChild(0);
    }

    public MSSyntaxTree getExpression() {
        return this.getChild(1);
    }
}