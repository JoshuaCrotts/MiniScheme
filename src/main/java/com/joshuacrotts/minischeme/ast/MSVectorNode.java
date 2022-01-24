package com.joshuacrotts.minischeme.ast;

import java.util.ArrayList;

/**
 * A vector is similar to a standard O(1) access-time array in most
 * languages. As of 1/23/22, vectors are not resizable.
 *
 * @author Joshua Crotts
 * @version 1/23/2022
 */
public class MSVectorNode extends MSSyntaxTree {

    /**
     * Number of elements allocated to this vector.
     */
    private final int NUM_ELEMENTS;

    public MSVectorNode(final ArrayList<MSSyntaxTree> elements) {
        super(MSNodeType.VECTOR);
        this.NUM_ELEMENTS = elements.size();
        elements.forEach(this::addChild);
    }

    @Override
    public MSSyntaxTree copy() {
        ArrayList<MSSyntaxTree> elementsCopy = new ArrayList<>();
        for (int i = 0; i < this.getChildrenSize(); i++) {
            elementsCopy.add(this.getChild(i).copy());
        }
        return new MSVectorNode(elementsCopy);
    }

    @Override
    public String getStringRep() {
        StringBuilder sb = new StringBuilder("#(");
        for (int i = 0; i < this.getChildrenSize() - 1; i++) {
            sb.append(this.getChild(i).getStringRep());
            sb.append(" ");
        }
        sb.append(this.getChild(this.getChildrenSize() - 1).getStringRep());
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String toString() {
        return this.getNodeType().toString();
    }

    public int size() {
        return this.NUM_ELEMENTS;
    }
}