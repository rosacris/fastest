package client.blogic.testing.atcal;

import client.blogic.testing.atcal.parser.AtcalBaseVisitor;
import client.blogic.testing.atcal.parser.AtcalParser;
import client.blogic.testing.atcal.z.ast.*;
import com.google.common.base.CharMatcher;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by Cristian on 3/31/15.
 */
public class ZExprEvaluator extends AtcalBaseVisitor<ZExpr> {

    private final ZExprSchema scope;

    public ZExprEvaluator(ZExprSchema scope) {
        this.scope = scope;
    }

    @Override
    public ZExpr visitZExprs(@NotNull AtcalParser.ZExprsContext ctx) {
        ZExpr zExpr = visit(ctx.zExpr());
        // if there is no more nested Z expressions return the evaluation result, otherwise create a nested scope and
        // recursively evaluate the nested Z expressions.
        if (ctx.zExprs() == null) {
            return zExpr;
        } else {
            ZExprSchema newZScope = ZExprSchema.add(scope, new ZVar("zScope", zExpr));
            ZExprEvaluator newZExprEval = new ZExprEvaluator(newZScope);
            return newZExprEval.visit(ctx.zExprs());
        }
    }

    @Override
    public ZExpr visitIdent(@NotNull AtcalParser.IdentContext ctx) {
        Optional<ZVar> var = scope.getVar(ctx.ID().getText());
        if (var.isPresent()) {
            return var.get().getValue();
        } else {
            throw new RuntimeException(String.format("Unknown identifier %s.", ctx.ID().getText()));
        }
    }

    @Override
    public ZExprNum visitNumLiteral(@NotNull AtcalParser.NumLiteralContext ctx) {
        return new ZExprNum(Long.parseLong(ctx.NUMBER().getText()));
    }

    @Override
    public ZExprString visitStrLiteral(@NotNull AtcalParser.StrLiteralContext ctx) {
        return new ZExprString(CharMatcher.is('\"').trimFrom(ctx.STRING().getText()));
    }

    @Override
    public ZExpr visitAutoExpr(@NotNull AtcalParser.AutoExprContext ctx) {
        return new ZExprAuto();
    }

    @Override
    public ZExpr visitElemExpr(@NotNull AtcalParser.ElemExprContext ctx) {
        ZExpr zScope = scope.getVar("zScope").get().getValue();
        if (zScope instanceof ZExprSet)
            return new ZExprList((ZExprSet) zScope);
        else
            throw new RuntimeException("Invalid use of @ELEM operator. The current Z scope is not a set.");
    }

    @Override
    public ZExpr visitGroup(@NotNull AtcalParser.GroupContext ctx) {
        return visit(ctx.zExpr());
    }

    // Cartessian product operations
    @Override
    public ZExpr visitProdProj(@NotNull AtcalParser.ProdProjContext ctx) {
        // TODO: Check that the return type is actually a cross product
        ZExprProd prod = (ZExprProd) visit(ctx.zExpr());
        return prod.getValue(Integer.valueOf(ctx.TUPPROJ().getText().substring(1)) - 1);
    }

    @Override
    public ZExpr visitProdCons(@NotNull AtcalParser.ProdConsContext ctx) {
        ArrayList<ZExpr> exprs = new ArrayList<ZExpr>(ctx.zExpr().size());
        for (AtcalParser.ZExprContext context : ctx.zExpr()) {
            exprs.add(visit(context));
        }
        return new ZExprProd(exprs.toArray(new ZExpr[]{}));
    }

    // Set operations
    @Override
    public ZExpr visitSetDom(@NotNull AtcalParser.SetDomContext ctx) {
        // TODO: Check that the return type is actually a set of pairs
        ZExprSet set = (ZExprSet) visit(ctx.zExpr());
        ArrayList<ZExpr> setDom = new ArrayList<ZExpr>();
        for (ZExpr elem : set) {
            setDom.add(((ZExprProd) elem).getValue(0));
        }
        return new ZExprSet(setDom.toArray(new ZExpr[]{}));
    }

    @Override
    public ZExpr visitSetElem(@NotNull AtcalParser.SetElemContext ctx) {
        // TODO: check that set is actually a set type
        ZExprSet set = (ZExprSet) visit(ctx.zExpr());
        return set.get(Integer.valueOf(ctx.NUMBER().getText()) - 1);
    }

    @Override
    public ZExpr visitSetDiff(@NotNull AtcalParser.SetDiffContext ctx) {
        // TODO: check that both operand types are sets
        ZExprSet leftOp = (ZExprSet) visit(ctx.zExpr(0));
        ZExprSet rightOp = (ZExprSet) visit(ctx.zExpr(1));
        return leftOp.difference(rightOp);
    }

    @Override
    public ZExpr visitSetInter(@NotNull AtcalParser.SetInterContext ctx) {
        // TODO: check that both operand types are sets
        ZExprSet leftOp = (ZExprSet) visit(ctx.zExpr(0));
        ZExprSet rightOp = (ZExprSet) visit(ctx.zExpr(1));
        return leftOp.intersection(rightOp);
    }

    @Override
    public ZExpr visitSetRan(@NotNull AtcalParser.SetRanContext ctx) {
        // TODO: Check that the return type is actually a set of pairs
        ZExprSet set = (ZExprSet) visit(ctx.zExpr());
        ArrayList<ZExpr> setRan = new ArrayList<ZExpr>();
        for (ZExpr elem : set) {
            setRan.add(((ZExprProd) elem).getValue(1));
        }
        return new ZExprSet(setRan.toArray(new ZExpr[]{}));
    }

    @Override
    public ZExpr visitSetProj(@NotNull AtcalParser.SetProjContext ctx) {
        // TODO: Check that the return type is actually a set of tuples
        ZExprSet set = (ZExprSet) visit(ctx.zExpr());
        ArrayList<ZExpr> setProj = new ArrayList<ZExpr>();
        for (ZExpr elem : set) {
            setProj.add(((ZExprProd) elem).getValue(Integer.valueOf(ctx.PROJ().getText().substring(1)) - 1));
        }
        return new ZExprSet(setProj.toArray(new ZExpr[]{}));
    }

    @Override
    public ZExpr visitSetCons(@NotNull AtcalParser.SetConsContext ctx) {
        ArrayList<ZExpr> exprs = new ArrayList<ZExpr>(ctx.zExpr().size());
        for (AtcalParser.ZExprContext context : ctx.zExpr()) {
            exprs.add(visit(context));
        }
        return new ZExprSet(exprs.toArray(new ZExpr[]{}));
    }

    @Override
    public ZExpr visitSetUnion(@NotNull AtcalParser.SetUnionContext ctx) {
        // TODO: check that both operand types are sets
        ZExprSet leftOp = (ZExprSet) visit(ctx.zExpr(0));
        ZExprSet rightOp = (ZExprSet) visit(ctx.zExpr(1));
        return leftOp.union(rightOp);
    }

    // Numerical operations

    @Override
    public ZExprNum visitNumMod(@NotNull AtcalParser.NumModContext ctx) {
        ZExprNum leftOp = (ZExprNum) visit(ctx.zExpr(0));
        ZExprNum rightOp = (ZExprNum) visit(ctx.zExpr(1));
        return new ZExprNum(leftOp.getNum() % rightOp.getNum());
    }

    @Override
    public ZExpr visitNumMul(@NotNull AtcalParser.NumMulContext ctx) {
        ZExprNum leftOp = (ZExprNum) visit(ctx.zExpr(0));
        ZExprNum rightOp = (ZExprNum) visit(ctx.zExpr(1));
        return new ZExprNum(leftOp.getNum() * rightOp.getNum());
    }

    @Override
    public ZExpr visitNumPlus(@NotNull AtcalParser.NumPlusContext ctx) {
        ZExprNum leftOp = (ZExprNum) visit(ctx.zExpr(0));
        ZExprNum rightOp = (ZExprNum) visit(ctx.zExpr(1));
        return new ZExprNum(leftOp.getNum() + rightOp.getNum());
    }

    @Override
    public ZExpr visitNumDiv(@NotNull AtcalParser.NumDivContext ctx) {
        ZExprNum leftOp = (ZExprNum) visit(ctx.zExpr(0));
        ZExprNum rightOp = (ZExprNum) visit(ctx.zExpr(1));
        return new ZExprNum(leftOp.getNum() / rightOp.getNum());
    }

    @Override
    public ZExpr visitNumMinus(@NotNull AtcalParser.NumMinusContext ctx) {
        ZExprNum leftOp = (ZExprNum) visit(ctx.zExpr(0));
        ZExprNum rightOp = (ZExprNum) visit(ctx.zExpr(1));
        return new ZExprNum(leftOp.getNum() - rightOp.getNum());
    }

    @Override
    public ZExprNum visitSetCard(@NotNull AtcalParser.SetCardContext ctx) {
        // TODO: check that the visitor returns an Z expression with set type
        return new ZExprNum(((ZExprSet) visit(ctx.zExpr())).getCard());
    }

    // Strings operations
    @Override
    public ZExprString visitStrConcat(@NotNull AtcalParser.StrConcatContext ctx) {
        String leftOpStr = ZExprString.fromZExpr(visit(ctx.zExpr(0))).getStr();
        String rightOpStr = ZExprString.fromZExpr(visit(ctx.zExpr(1))).getStr();
        return new ZExprString(leftOpStr + rightOpStr);
    }
}
