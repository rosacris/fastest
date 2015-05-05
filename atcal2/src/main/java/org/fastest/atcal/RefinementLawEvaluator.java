package org.fastest.atcal;

import com.google.common.collect.Lists;
import org.antlr.v4.runtime.misc.NotNull;
import org.fastest.atcal.apl.*;
import org.fastest.atcal.parser.AtcalBaseVisitor;
import org.fastest.atcal.parser.AtcalParser;
import org.fastest.atcal.z.ast.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Cristian on 4/16/15.
 */
public class RefinementLawEvaluator extends AtcalBaseVisitor<List<APLExpr>> {

    private final ZExprSchema zScope;   // Evaluator's scope for z expressions
    private final APLLValue aplScope;      // Evaluator's scope for APL code
    private final Map<String, ATCALType> types; // Evaluator's declared types

    public RefinementLawEvaluator(ZExprSchema zScope, APLLValue aplScope, Map<String, ATCALType> types) {
        this.zScope = zScope;
        this.aplScope = aplScope;
        this.types = types;
    }

    private static APLExpr ZExprToAPLExpr(ZExpr zExpr) throws Exception {
        ZExprConst exp = zExpr instanceof ZExprConst ? ((ZExprConst) zExpr) : null;
        if (exp != null)
            return new ConsExpr(exp.getValue());

        ZExprNum exp2 = zExpr instanceof ZExprNum ? ((ZExprNum) zExpr) : null;
        if (exp2 != null)
            return new IntExpr(exp2.getNum());

        ZExprString exp3 = zExpr instanceof ZExprString ? ((ZExprString) zExpr) : null;
        if (exp3 != null)
            return new StringExpr(exp3.getStr());

        throw new Exception();
    }

    private ZExpr getScope() {
        return this.zScope.getVar("zScope").get().getValue();
    }

    @Override
    public List<APLExpr> visitLawRefinement(@NotNull AtcalParser.LawRefinementContext ctx) {
        // Evaluate the Z expressions of the law
        ZExprEvaluator zExprEvaluator = new ZExprEvaluator(zScope);
        ZExpr zExpr = zExprEvaluator.visit(ctx.zExpr());

        // Create a new zScope that includes the result of evaluating the Z expressions of the law
        ZExprSchema newScope = ZExprSchema.add(zScope, new ZVar("zScope", zExpr));

        // Recursively evaluate the refinements of the law with the new Z scope and the current APL scope
        // The evaluation of each refinement produces a block of intermediate code that is collected to produce the output.
        List<APLExpr> codeBlock = Lists.newArrayList();
        RefinementLawEvaluator lawEvaluator = new RefinementLawEvaluator(newScope, aplScope, types);
        for (AtcalParser.RefinementContext context : ctx.refinement())
            codeBlock.addAll(lawEvaluator.visit(context));

        return codeBlock;
    }

    @Override
    public List<APLExpr> visitVarLValue(@NotNull AtcalParser.VarLValueContext ctx) {
        return Lists.newArrayList((APLExpr) new APLVar(ctx.ID().getText()));
    }

    @Override
    public List<APLExpr> visitArrayLValue(@NotNull AtcalParser.ArrayLValueContext ctx) {
        if (ctx.NUMBER() != null) {
            return Lists.newArrayList((APLExpr) new APLArray(aplScope.getName(), Integer.valueOf(ctx.NUMBER().getText())));
        } else {
            return Lists.newArrayList((APLExpr) new APLVar(aplScope + "[]"));
        }
    }

    @Override
    public List<APLExpr> visitImplRef(@NotNull AtcalParser.ImplRefContext ctx) {
        try {
            List<APLExpr> lvalueCodeBlock = visit(ctx.lvalue());
            APLLValue lvalue = (APLLValue) lvalueCodeBlock.get(0);
            if (ctx.asRef() == null) {
                return Lists.newArrayList((APLExpr) new AssignStmt(lvalue, ZExprToAPLExpr(this.getScope())));
            } else {
                RefinementLawEvaluator newScopeEvaluator = new RefinementLawEvaluator(zScope, lvalue, types);
                return newScopeEvaluator.visit(ctx.asRef());
            }
        } catch (Exception e) {
            System.out.println("Unimplemented ZExpr translation.");
        }
        return Lists.newArrayList();    // This should not happen!
    }

    @Override
    public List<APLExpr> visitWithRef(@NotNull AtcalParser.WithRefContext ctx) {
        List<APLExpr> codeBlock = Lists.newArrayList();

        ATCALType asType = null;
        String typeId = null;
        if ((typeId = ctx.ID().getText()) != null)
            asType = types.get(typeId);

        // TODO : if type is defined in the refinement law, parse it with ATCAL's type visitor
        // Generate code according to the type of the refinement variable

        // Contract types are used to create complex data structures that have a contract (an interface).
        // They have a constructor, a getter and a setter function that instantiate, extract or insert values.
        if (asType instanceof ContractType) {
            ContractType type = (ContractType) asType;

            // Create a new temporal variable to hold the data structure under construction.
            APLVar var = new APLVar(aplScope.getName() + '_' + type.getSetterArgs().get(0));
            codeBlock.add(new AssignStmt(var, new CallExpr(type.getConstructor(), Lists.newArrayList(""))));

            // Evaluate the WITH-clauses. The evaluation must produce a block of code that defines one variable for each
            // argument of the setter function in the contract type.
            for (AtcalParser.LawRefinementContext lawRefinementContext : ctx.lawRefinement()) {
                codeBlock.addAll(visit(lawRefinementContext));
            }

            // Insert the values of the refined WITH-clauses into the data structure using the provided setter function
            // TODO: check the type values with the types of the arguments (the amount of them must also match!)
            List<String> args = Lists.newArrayList(aplScope.getName() + '_' + type.getSetterArgs().get(0));
            args.addAll(type.getSetterArgs().subList(1, type.getSetterArgs().size()));
            codeBlock.add(new CallExpr(type.getSetter(), args));

            // Assign the temporal variable holding the data structure to the real refinement variable.
            codeBlock.add(new AssignStmt(aplScope, var));

        // Array types are handled as a special case because they often have native support in the target language.
        } else if (asType instanceof ArrayType) {
            ArrayType type = (ArrayType) asType;

            // Allocate a new array using the APL built-in function newArray and assign it to the current APL variable in scope.
            codeBlock.add(new AssignStmt(aplScope, new CallExpr("newArray", Lists.newArrayList(String.valueOf(type.getSize())))));

            // Evaluate the WITH-clauses. The evaluation produces a block of code that assign values to many indices in
            // the array (or all).
            for (AtcalParser.LawRefinementContext lawRefinementContext : ctx.lawRefinement()) {
                codeBlock.addAll(visit(lawRefinementContext));
            }
        }
        return codeBlock;
    }

    @Override
    public List<APLExpr> visitZExprRef(@NotNull AtcalParser.ZExprRefContext ctx) {
        return visitLawRefinement(ctx.lawRefinement());
    }
}