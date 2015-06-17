package org.fastest.atcal;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.fastest.atcal.parser.AtcalBaseVisitor;
import org.fastest.atcal.parser.AtcalParser;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Cristian on 15/05/15.
 */
public class TypesEvaluator extends AtcalBaseVisitor<Map<String, ATCALType>> {

    // Helper function to simplify converting lists of terminal nodes into lists of strings
    private static final Function<TerminalNode, String> TERMINAL_TOSTRING = new Function<TerminalNode, String>() {
        @Override
        public String apply(TerminalNode o) {
            return o.getText();
        }
    };
    private final TypeEvaluator typeEval;
    private final Map<String, ATCALType> types;

    public TypesEvaluator() {
        // preload the default data types (INT, FLOAT, STRING) in the type namespace.
        this.types = Maps.newHashMap();
        types.put("INT", new IntType());
        types.put("FLOAT", new FloatType());
        types.put("STRING", new StringType());

        this.typeEval = new TypeEvaluator(types);
    }

    @Override
    public Map<String, ATCALType> visitDatatypes(@NotNull AtcalParser.DatatypesContext ctx) {
        for (AtcalParser.TypeDecContext typeDecContext : ctx.typeDec()) {
            types.put(typeDecContext.ID().getText(), this.typeEval.visit(typeDecContext.type()));
        }
        return types;
    }

    /**
     * This method is only used for unit testing purposes. The normal type checking flow at runtime calls the method
     * above that will skip this method to visit types directly (to avoid unpacking the single entry map returned by
     * this method).
     */
    @Override
    public Map<String, ATCALType> visitTypeDec(@NotNull AtcalParser.TypeDecContext ctx) {
        return ImmutableMap.of(ctx.ID().getText(), this.typeEval.visit(ctx.type()));
    }

    // Private nested class to evaluate individual types
    public static class TypeEvaluator extends AtcalBaseVisitor<ATCALType> {

        private final Map<String, ATCALType> types;

        public TypeEvaluator(Map<String, ATCALType> types) {
            this.types = types;
        }

        @Override
        public ATCALType visitNameType(@NotNull AtcalParser.NameTypeContext ctx) {
            if (!types.containsKey(ctx.getText()))
                throw new RuntimeException("Unknown type " + ctx.getText());
            else
                return types.get(ctx.getText());
        }

        @Override
        public ATCALType visitIntType(@NotNull AtcalParser.IntTypeContext ctx) {
            return new IntType();
        }

        @Override
        public ATCALType visitFloatType(@NotNull AtcalParser.FloatTypeContext ctx) {
            return new FloatType();
        }

        @Override
        public ATCALType visitStringType(@NotNull AtcalParser.StringTypeContext ctx) {
            return new StringType();
        }

        @Override
        public ATCALType visitArrayType(@NotNull AtcalParser.ArrayTypeContext ctx) {
            ATCALType type = visit(ctx.type());
            return new ArrayType(type, Integer.valueOf(ctx.NUMBER().getText()));
        }

        @Override
        public ATCALType visitEnumType(@NotNull AtcalParser.EnumTypeContext ctx) {
            String enumName = ctx.ID().getText();
            Collection<String> elements = Collections2.transform(ctx.args().ID(), TERMINAL_TOSTRING);
            return new EnumType(enumName, elements);
        }

        @Override
        public ATCALType visitRecordType(@NotNull AtcalParser.RecordTypeContext ctx) {
            Map<String, ATCALType> fields = Maps.newHashMap();
            for (int i = 1; i < ctx.ID().size(); i++) {
                fields.put(ctx.ID(i).toString(), visit(ctx.type(i - 1)));
            }
            return new RecordType(fields);
        }

        @Override
        public ATCALType visitContractType(@NotNull AtcalParser.ContractTypeContext ctx) {
            return new ContractType(ctx.ID(0).getText(), Lists.transform(ctx.args(0).ID(), TERMINAL_TOSTRING),
                    ctx.ID(1).getText(), Lists.transform(ctx.args(1).ID(), TERMINAL_TOSTRING),
                    ctx.ID(2).getText(), Lists.transform(ctx.args(2).ID(), TERMINAL_TOSTRING));
        }
    }
}