package com.joshuacrotts.minischeme.ast;

import java.util.ArrayList;

public class MSCallNode extends MSSyntaxTree {

    /**
     * Number of arguments passed to the procedure being called, if any.
     */
    private int numProcArgs;

    /**
     * Number of arguments passed to the lambda being called, if any.
     */
    private int numLambdaArgs;

    public MSCallNode(MSSyntaxTree procIdentifier,
                        ArrayList<MSSyntaxTree> procArgs,
                        ArrayList<MSSyntaxTree> lambdaArgs) {
        super(MSNodeType.CALL);
        this.addChild(procIdentifier);
        this.numProcArgs = procArgs.size();
        this.numLambdaArgs = lambdaArgs.size();

        for (int i = 0; i < this.numProcArgs; i++) {
            this.addChild(procArgs.get(i));
        }

        for (int i = 0; i < this.numLambdaArgs; i++) {
            this.addChild(lambdaArgs.get(i));
        }
    }

    @Override
    public MSSyntaxTree copy() {
        MSSyntaxTree idCopy = this.getChild(0).copy();
        ArrayList<MSSyntaxTree> procArgsCopy = new ArrayList<>();
        for (int i = 0; i < this.numProcArgs; i++) {
            procArgsCopy.add(this.getChild(i + 1).copy());
        }

        ArrayList<MSSyntaxTree> lambdaArgsCopy = new ArrayList<>();
        for (int i = 0; i < this.numLambdaArgs; i++) {
            lambdaArgsCopy.add(this.getChild(i + 1 + this.numProcArgs).copy());
        }

        return new MSCallNode(idCopy, procArgsCopy, lambdaArgsCopy);
    }

    @Override
    public String getStringRep() {
        return this.getNodeType().toString();
    }

    @Override
    public String toString() {
        return this.getNodeType().toString();
    }

    public MSIdentifierNode getIdentifier() {
        return (MSIdentifierNode) this.getChild(0);
    }

    public ArrayList<MSSyntaxTree> getProcedureArguments() {
        ArrayList<MSSyntaxTree> procArgs = new ArrayList<>();
        for (int i = 0; i < this.numProcArgs; i++) {
            procArgs.add(this.getChild(i + 1));
        }
        return procArgs;
    }

    public ArrayList<MSSyntaxTree> getLambdaArguments() {
        ArrayList<MSSyntaxTree> lambdaArgs = new ArrayList<>();
        for (int i = 0; i < this.numLambdaArgs; i++) {
            lambdaArgs.add(this.getChild(i + 1 + this.numProcArgs));
        }
        return lambdaArgs;
    }
}