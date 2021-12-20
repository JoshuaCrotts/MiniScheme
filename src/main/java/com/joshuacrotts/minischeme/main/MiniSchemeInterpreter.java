package com.joshuacrotts.minischeme.main;

import com.joshuacrotts.minischeme.MiniSchemeParser;
import com.joshuacrotts.minischeme.ast.MSBooleanLitNode;
import com.joshuacrotts.minischeme.ast.MSDoubleLitNode;
import com.joshuacrotts.minischeme.ast.MSIdentifierNode;
import com.joshuacrotts.minischeme.ast.MSNodeType;
import com.joshuacrotts.minischeme.ast.MSOpExpression;
import com.joshuacrotts.minischeme.ast.MSPairNode;
import com.joshuacrotts.minischeme.ast.MSProcedureCallNode;
import com.joshuacrotts.minischeme.ast.MSProcedureDefinitionNode;
import com.joshuacrotts.minischeme.ast.MSStringLitNode;
import com.joshuacrotts.minischeme.ast.MSSyntaxTree;
import com.joshuacrotts.minischeme.main.LValue.LValueType;
import com.joshuacrotts.minischeme.parser.MSListener;
import com.joshuacrotts.minischeme.parser.MSSemanticError;
import java.util.ArrayList;

public class MiniSchemeInterpreter {

    /**
     *
     */
    private final MSSyntaxTree tree;

    public MiniSchemeInterpreter(MSSyntaxTree tree) {
        this.tree = tree;
    }

    /**
     *
     */
    public void execute() {
        for (MSSyntaxTree ch : this.tree.getChildren()) {
            LValue lhs = this.interpretTree(ch);
            switch (lhs.getType()) {
                case NUM:
                case BOOL:
                case PAIR:
                case STR:
                    System.out.println(lhs);
                    break;
            }
        }
    }

    /**
     * @param procDef
     * @param body
     * @param args
     */
    private static void replaceParams(MSProcedureDefinitionNode procDef,
        MSSyntaxTree body, ArrayList<MSSyntaxTree> args) {
        for (int i = 0; i < args.size(); i++) {
            replaceParamsHelper(procDef, body, args.get(i), i);
        }
    }

    /**
     * @param procDef
     * @param body
     * @param arg
     * @param replaceIdx
     */
    private static void replaceParamsHelper(MSProcedureDefinitionNode procDef,
        MSSyntaxTree body, MSSyntaxTree arg, int replaceIdx) {
        for (int i = 0; i < body.getChildrenSize(); i++) {
            MSSyntaxTree child = body.getChild(i);
            if (child.getNodeType() == MSNodeType.ID) {
                MSIdentifierNode id = (MSIdentifierNode) child;
                if (procDef.getArgumentLoc(id.getIdentifier()) == replaceIdx) {
                    body.setChild(i, arg);
                }
            } else {
                replaceParamsHelper(procDef, child, arg, replaceIdx);
            }
        }
    }

    /**
     * @param tree
     * @return
     */
    protected LValue interpretTree(MSSyntaxTree tree) {
        if (tree == null) {
            return new LValue(LValue.LValueType.NULL);
        }
        try {
            switch (tree.getNodeType()) {
                case ROOT:
                    return this.interpretTree(tree.getChild(0));
                case ID:
                    return this.interpretIdentifier(tree);
                case OP:
                    return this.interpretOperator(tree);
                case NUM:
                    return this.interpretNumber(tree);
                case BOOL:
                    return this.interpretBoolean(tree);
                case STR:
                    return this.interpretString(tree);
                case PAIR:
                    return this.interpretPair(tree);
                case LIST:
                    return this.interpretList(tree);
                case IF:
                    return this.interpretIf(tree);
                case COND:
                    return this.interpretCond(tree);
                case PROCCALL:
                    return this.interpretProcCall(tree);
            }
        } catch (MSSemanticError err) {
            System.err.println(err.getMessage());
        }

        return new LValue();
    }

    /**
     * @param tree
     */
    private LValue interpretNumber(MSSyntaxTree tree) {
        return new LValue(((MSDoubleLitNode) tree));
    }

    /**
     * @param tree
     * @return
     */
    private LValue interpretBoolean(MSSyntaxTree tree) {
        return new LValue(((MSBooleanLitNode) tree));
    }

    /**
     *
     * @param tree
     * @return
     */
    private LValue interpretString(MSSyntaxTree tree) {
        return new LValue((MSStringLitNode) tree);
    }

    /**
     * @param tree
     * @return
     */
    private LValue interpretPair(MSSyntaxTree tree) throws MSSemanticError {
        MSPairNode pairNode = (MSPairNode) tree;
        // Evaluate the CAR and CDR.
        LValue carEval = this.interpretTree(pairNode.getCar());
        LValue cdrEval = this.interpretTree(pairNode.getCdr());

        MSSyntaxTree carNode = null;
        MSSyntaxTree cdrNode = null;
        switch (carEval.getType()) {
            case NUM:
                carNode = new MSDoubleLitNode(carEval.getDoubleValue());
                break;
            case BOOL:
                carNode = new MSBooleanLitNode(carEval.getBoolValue());
                break;
            case STR:
                carNode = new MSStringLitNode(carEval.getStringValue());
                break;
            case PAIR:
                carNode = carEval.getTreeValue();
                break;
            case NULL:
                break;
            default:
                throw new UnsupportedOperationException(
                    "Cannot make a pair out of datatype " + carEval.getType() + " yet.");
        }

        switch (cdrEval.getType()) {
            case NUM:
                cdrNode = new MSDoubleLitNode(cdrEval.getDoubleValue());
                break;
            case BOOL:
                cdrNode = new MSBooleanLitNode(cdrEval.getBoolValue());
                break;
            case STR:
                cdrNode = new MSStringLitNode(cdrEval.getStringValue());
                break;
            case PAIR:
                cdrNode = cdrEval.getTreeValue();
                break;
            case NULL:
                break;
            default:
                throw new UnsupportedOperationException(
                    "Cannot make a pair out of datatype " + cdrEval.getType() + " yet.");
        }

        return new LValue(new MSPairNode(MSNodeType.PAIR, carNode, cdrNode));
    }

    /**
     * @param tree
     * @return
     * @throws MSSemanticError
     */
    private LValue interpretList(MSSyntaxTree tree) throws MSSemanticError {
        MSPairNode rootPair = (MSPairNode) tree;
        // We need to evaluate every element of the "list".
        LValue carEval = this.interpretTree(rootPair.getCar());
        LValue cdrEval = this.interpretTree(rootPair.getCdr());

        MSSyntaxTree carNode = null;
        MSSyntaxTree cdrNode = null;
        switch (carEval.getType()) {
            case NUM:
                carNode = new MSDoubleLitNode(carEval.getDoubleValue());
                break;
            case BOOL:
                carNode = new MSBooleanLitNode(carEval.getBoolValue());
                break;
            case STR:
                carNode = new MSStringLitNode(carEval.getStringValue());
                break;
            case PAIR:
                carNode = carEval.getTreeValue();
                break;
            case NULL:
                break;
            default:
                throw new UnsupportedOperationException(
                    "Cannot make a list out of datatype " + carEval.getType() + " yet.");
        }

        switch (cdrEval.getType()) {
            case NUM:
                cdrNode = new MSDoubleLitNode(cdrEval.getDoubleValue());
                break;
            case BOOL:
                cdrNode = new MSBooleanLitNode(cdrEval.getBoolValue());
                break;
            case STR:
                cdrNode = new MSStringLitNode(cdrEval.getStringValue());
            case PAIR:
                cdrNode = cdrEval.getTreeValue();
                break;
            case NULL:
                break;
            default:
                throw new UnsupportedOperationException(
                    "Cannot make a list out of datatype " + cdrEval.getType() + " yet.");
        }
        return new LValue(new MSPairNode(MSNodeType.LIST, carNode, cdrNode));
    }

    /**
     * @param tree
     * @return
     */
    private LValue interpretOperator(MSSyntaxTree tree) {
        int opType = ((MSOpExpression) tree).getOpType();
        LValue res = null;
        // Determine if it's a unary operator or nary.
        if (tree.getChildrenSize() == 1) {
            res = this.interpretPrimitiveUnaryOp(this.interpretTree(tree.getChild(0)), opType);
        } else {
            res = this.interpretTree(tree.getChild(0));
            for (int i = 1; i < tree.getChildrenSize(); i++) {
                res = this
                    .interpretPrimitiveBinaryOp(res, opType, this.interpretTree(tree.getChild(i)));
            }
        }
        return res;
    }

    /**
     * @param tree
     * @return
     */
    private LValue interpretIdentifier(MSSyntaxTree tree) {
        String id = tree.getStringRep();
        if (MSListener.symbolTable.isVariable(id)) {
            return this.interpretTree(MSListener.symbolTable.getVariable(id).getExpression().getChild(1));
        } else {
            MSProcedureDefinitionNode procDef = (MSProcedureDefinitionNode) MSListener.symbolTable.getProcedure(id).getProcDef();
            return new LValue(LValueType.PROCCALL, procDef.getIdentifier());
        }
    }

    /**
     * @param tree
     * @return
     */
    private LValue interpretIf(MSSyntaxTree tree) {
        LValue ifCond = this.interpretTree(tree.getChild(0));
        if (ifCond.getType() == LValue.LValueType.BOOL) {
            return ifCond.getBoolValue()
                    ? this.interpretTree(tree.getChild(1))
                    : this.interpretTree(tree.getChild(2));
        }
        return null;
    }

    /**
     * @param tree
     * @return
     */
    private LValue interpretCond(MSSyntaxTree tree) {
        int condIdx = 0;
        int bodyIdx = 1;
        boolean execLastBlock = true;

        while (condIdx < tree.getChildrenSize()
            && bodyIdx < tree.getChildrenSize()) {
            LValue condCond = this.interpretTree(tree.getChild(condIdx));
            // If the condition is true, evaluate that expression.
            if (condCond.getBoolValue()) {
                execLastBlock = false;
                break;
            } else {
                condIdx += 2;
                bodyIdx += 2;
            }
        }

        bodyIdx = execLastBlock ? bodyIdx - 1 : bodyIdx;
        return this.interpretTree(tree.getChild(bodyIdx));
    }

    /**
     * @param tree
     * @return
     */
    private LValue interpretProcCall(MSSyntaxTree tree) {
        MSProcedureCallNode procCall = (MSProcedureCallNode) tree;
        String id = procCall.getIdentifier().getStringRep();
        MSProcedureDefinitionNode def = (MSProcedureDefinitionNode)
            MSListener.symbolTable.getProcedure(id).getProcDef();
        ArrayList<MSSyntaxTree> args = new ArrayList<>();
        for (int i = 0; i < procCall.getArguments().size(); i++) {
            LValue lhs = this.interpretTree(procCall.getArguments().get(i));
            if (lhs.getType() == LValue.LValueType.NUM) {
                args.add(new MSDoubleLitNode(lhs.getDoubleValue()));
            } else if (lhs.getType() == LValue.LValueType.BOOL) {
                args.add(new MSBooleanLitNode(lhs.getBoolValue()));
            } else if (lhs.getType() == LValueType.STR) {
                args.add(new MSStringLitNode(lhs.getStringValue()));
            } else if (lhs.getType() == LValueType.PROCCALL) {
                args.add(lhs.getTreeValue());
            } else if (lhs.getType() == LValueType.PAIR) {
                // If it is null, then evaluate the null list.
                if (lhs.getTreeValue() == null) {
                    args.add(new MSPairNode());
                } else {
                    args.add(lhs.getTreeValue().copy());
                }
            }
        }

        MSSyntaxTree body = def.getBody().copy();
        replaceParams(def, body, args);
        return this.interpretTree(body);
    }

    /**
     * @param lhs
     * @param opType
     * @param rhs
     * @return
     */
    private LValue interpretPrimitiveBinaryOp(LValue lhs, int opType, LValue rhs) {
        switch (opType) {
            case MiniSchemeParser.PLUS:
                return new LValue(lhs.getDoubleValue() + rhs.getDoubleValue());
            case MiniSchemeParser.MINUS:
                return new LValue(lhs.getDoubleValue() - rhs.getDoubleValue());
            case MiniSchemeParser.STAR:
                return new LValue(lhs.getDoubleValue() * rhs.getDoubleValue());
            case MiniSchemeParser.SLASH:
                return new LValue(lhs.getDoubleValue() / rhs.getDoubleValue());
            case MiniSchemeParser.MODULO:
                return new LValue(lhs.getDoubleValue() % rhs.getDoubleValue());
            case MiniSchemeParser.EXPONENTIATION:
                return new LValue(Math.pow(lhs.getDoubleValue(), rhs.getDoubleValue()));
            case MiniSchemeParser.LOGICAL_EQ:
                return new LValue(lhs.getDoubleValue() == rhs.getDoubleValue());
            case MiniSchemeParser.LOGICAL_NE:
                return new LValue(lhs.getDoubleValue() != rhs.getDoubleValue());
            case MiniSchemeParser.LOGICAL_LT:
                return new LValue(lhs.getDoubleValue() < rhs.getDoubleValue());
            case MiniSchemeParser.LOGICAL_LE:
                return new LValue(lhs.getDoubleValue() <= rhs.getDoubleValue());
            case MiniSchemeParser.LOGICAL_GT:
                return new LValue(lhs.getDoubleValue() > rhs.getDoubleValue());
            case MiniSchemeParser.LOGICAL_GE:
                return new LValue(lhs.getDoubleValue() >= rhs.getDoubleValue());
            case MiniSchemeParser.STRING_APPEND:
                return new LValue(lhs.getStringValue() + rhs.getStringValue());
            case MiniSchemeParser.EQ_FN:
                return this.interpretEqFn(lhs, rhs);
            case MiniSchemeParser.EQUAL_FN:
                return this.interpretEqualFn(lhs, rhs);
        }

        throw new IllegalArgumentException("ERR invalid binop type " + opType);
    }

    /**
     * @param lhs
     * @param opType
     * @return
     */
    private LValue interpretPrimitiveUnaryOp(LValue lhs, int opType) {
        switch (opType) {
            case MiniSchemeParser.DISPLAY:
                System.out.println(lhs.toDisplayString());
                return new LValue(LValueType.DISP);
            case MiniSchemeParser.SIN:
                return new LValue(Math.sin(lhs.getDoubleValue()));
            case MiniSchemeParser.COS:
                return new LValue(Math.cos(lhs.getDoubleValue()));
            case MiniSchemeParser.TAN:
                return new LValue(Math.tan(lhs.getDoubleValue()));
            case MiniSchemeParser.ASIN:
                return new LValue(Math.asin(lhs.getDoubleValue()));
            case MiniSchemeParser.ACOS:
                return new LValue(Math.acos(lhs.getDoubleValue()));
            case MiniSchemeParser.ATAN:
                return new LValue(Math.atan(lhs.getDoubleValue()));
            case MiniSchemeParser.SQRT:
                return new LValue(Math.sqrt(lhs.getDoubleValue()));
            case MiniSchemeParser.NOT:
                return new LValue(!lhs.getBoolValue());
            case MiniSchemeParser.CAR:
                return new LValue(((MSPairNode) lhs.getTreeValue()).getCar());
            case MiniSchemeParser.CDR:
                return new LValue(((MSPairNode) lhs.getTreeValue()).getCdr());
            case MiniSchemeParser.NULL_FN:
                return new LValue(lhs.getTreeValue() == null || ((MSPairNode) lhs.getTreeValue()).isNull());
            case MiniSchemeParser.NUMBER_FN:
                return new LValue(lhs.getType() == LValueType.NUM);
            case MiniSchemeParser.BOOL_FN:
                return new LValue(lhs.getType() == LValueType.BOOL);
            case MiniSchemeParser.STRING_FN:
                return new LValue(lhs.getType() == LValueType.STR);
        }

        throw new IllegalArgumentException("ERR invalid unary type " + opType);
    }

    /**
     *
     * @param lhs
     * @param rhs
     * @return
     */
    private LValue interpretEqualFn(LValue lhs, LValue rhs) {
        if (lhs.getType() == rhs.getType()) {
            switch (lhs.getType()) {
                case NUM: return new LValue(lhs.getDoubleValue() == rhs.getDoubleValue());
                case BOOL: return new LValue(lhs.getBoolValue() == rhs.getBoolValue());
                case STR: return new LValue(lhs.getStringValue().equals(rhs.getStringValue()));
                case PAIR:
                case NULL:
                default:
                    throw new UnsupportedOperationException("Not yet!");
            }
        }
        return new LValue(false);
    }

    /**
     *
     * @param lhs
     * @param rhs
     * @return
     */
    private LValue interpretEqFn(LValue lhs, LValue rhs) {
        return new LValue((lhs.getType() == rhs.getType())
                && ((lhs == rhs) || (lhs.getDoubleValue() == rhs.getDoubleValue())));
    }
}