package com.joshuacrotts.minischeme.ast;

import java.util.ArrayList;

/**
 *
 *
 * @author Joshua Crotts
 * @version 12/30/2021
 */
public class MSLetDeclarationNode extends MSDeclaration {

    /**
     * Number of variables declared in this let block.
     */
    private int numDeclarations;

    /**
     * Keeps track of what "type" of let declaration this is. For instance,
     * let vs let* vs letrec.
     */
    private int letType;

    public MSLetDeclarationNode(int letType,
                                ArrayList<MSSyntaxTree> variableDeclarations,
                                MSSyntaxTree letBody) {
        super(MSNodeType.LET_DECL);
        this.letType = letType;
        this.numDeclarations = variableDeclarations.size();
        variableDeclarations.forEach(this::addChild);
        this.addChild(letBody);
    }

    @Override
    public MSSyntaxTree copy() {
        ArrayList<MSSyntaxTree> variableDeclarations = new ArrayList<>();
        for (int i = 0; i < this.numDeclarations; i++) {
            variableDeclarations.add(this.getChild(i).copy());
        }
        MSSyntaxTree letBodyCopy = this.getChild(this.getChildrenSize() - 1).copy();
        return new MSLetDeclarationNode(this.letType, variableDeclarations, letBodyCopy);
    }

    public int getLetType() {
        return this.letType;
    }

    @Override
    public String getStringRep() {
        return this.getNodeType().toString();
    }

    @Override
    public String toString() {
        return this.getNodeType().toString();
    }

}
