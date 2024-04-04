package org.basex.query.func.fn;

import static org.basex.query.QueryError.*;
import static org.basex.query.value.type.SeqType.*;

import java.io.*;
import java.util.*;

import org.basex.api.dom.*;
import org.basex.io.*;
import org.basex.query.*;
import org.basex.query.expr.*;
import org.basex.query.func.*;
import org.basex.query.util.list.*;
import org.basex.query.value.item.*;
import org.basex.query.value.node.*;
import org.basex.query.value.type.*;
import org.basex.query.var.*;
import org.basex.util.*;
import org.basex.util.hash.*;
import org.basex.util.options.*;

import de.bottlecaps.markup.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-24, BSD License
 * @author Gunther Rademacher
 * @author Nico verwer
 */
public final class FnTransparentInvisibleXml extends StandardFunc {
  /** The invisible XML parser generator. */
  private Generator generator;

  @Override
  public FuncItem item(final QueryContext qc, final InputInfo ii) throws QueryException {
    if(generator == null) {
      for(final String className : Arrays.asList("de.bottlecaps.markup.Blitz",
          "de.bottlecaps.markup.BlitzException", "de.bottlecaps.markup.BlitzParseException")) {
        if(!Reflect.available(className)) {
          throw BASEX_CLASSPATH_X_X.get(info, definition.local(), className);
        }
      }
      generator = new Generator();
    }
    return generator.generate(qc);
  }

  /**
   * Invisible XML parser generator.
   */
  private final class Generator {
    /**
     * Generate a parser from an invisible XML grammar.
     * @param qc query context
     * @return the parsing function
     * @throws QueryException query exception
     */
    public FuncItem generate(final QueryContext qc) throws QueryException {
      final Item value = arg(0).atomItem(qc, info);
      final String grammar = value.isEmpty()
          ? Blitz.ixmlGrammar()
          : FnTransparentInvisibleXml.this.toString(value);
      final IxmlOptions opts = toOptions(arg(1), new IxmlOptions(), qc);
      final Blitz.Option[] blitzOpts = opts.toBlitzOptions();
      final de.bottlecaps.markup.blitz.Parser parser;
      try {
        parser = Blitz.generate(grammar, blitzOpts);
      } catch(final BlitzParseException ex) {
        throw IXML_GRM_X_X_X.get(info, ex.getOffendingToken(), ex.getLine(), ex.getColumn());
      } catch(final BlitzException ex) {
        throw IXML_GEN_X.get(info, ex);
      }
      final Var[] params = { new VarScope().addNew(new QNm("input"), ELEMENT_O, true, qc, info)}; // Type of the argument of the generated parse function
      final Expr arg = new VarRef(info, params[0]);
      final ParseTransparentInvisibleXml parseFunction = new ParseTransparentInvisibleXml(info, parser, arg);
      final FuncType ft = FuncType.get(parseFunction.seqType(), ELEMENT_O); // Type of the generated parse function; result of fn:transparent-invisible-xml
      return new FuncItem(info, parseFunction, params, AnnList.EMPTY, ft, params.length, null);
    }
  }

  /**
   * Result function of fn:transparent-invisible-xml: parse invisible XML input.
   */
  private static final class ParseTransparentInvisibleXml extends Arr {
    /** Generated invisible XML parser. */
    private final de.bottlecaps.markup.blitz.Parser parser;

    /**
     * Constructor.
     * @param info input info (can be {@code null})
     * @param args function arguments
     * @param parser generated invisible XML parser
     */
    private ParseTransparentInvisibleXml(final InputInfo info, final de.bottlecaps.markup.blitz.Parser parser, final Expr... args) {
      super(info, DOCUMENT_NODE_O, args);
      this.parser = parser;
    }

    @Override
    public DBNode item(final QueryContext qc, final InputInfo ii) throws QueryException {
      final ANode inputElement = toElem(arg(0), qc);
      BXElem bxElement = (BXElem) inputElement.toJava();
      try {
        final String output = parser.parse(bxElement);
        return new DBNode(IO.get(output));
      } catch(final BlitzParseException ex) {
        throw IXML_INP_X_X_X.get(ii, ex.getOffendingToken(), ex.getLine(), ex.getColumn());
      } catch(BlitzException | IOException ex) {
        throw IXML_RESULT_X.get(info, ex);
      }
    }

    @Override
    public Expr copy(final CompileContext cc, final IntObjMap<Var> vm) {
      return copyType(new ParseTransparentInvisibleXml(info, parser, copyAll(cc, vm, args())));
    }

    @Override
    public void toString(final QueryString qs) {
      qs.token("parse-invisible-xml").params(exprs);
    }
  }

  /**
   * Options for fn:transparent-invisible-xml.
   */
  public static final class IxmlOptions extends Options {
    /** Raise an error if the parse function fails. */
    public static final BooleanOption FAIL_ON_ERROR = new BooleanOption("fail-on-error", false);
    /** Find longest matching substring in the input. */
    public static final BooleanOption LONGEST_MATCH = new BooleanOption("longest-match", false);
    /** Find shortest matching substring in the input. */
    public static final BooleanOption SHORTEST_MATCH = new BooleanOption("shortest-match", false);
    /** Allow multiple matches of the grammar on the input. */
    public static final BooleanOption MULTIPLE_MATCHES = new BooleanOption("multiple-matches", false);
    /** Skip unmatched words (sequences of non-space characters) in the input. */
    public static final BooleanOption SKIP_UNMATCHED_WORDS = new BooleanOption("skip-unmatched-words", false);

    public Blitz.Option[] toBlitzOptions() {
      ArrayList<Blitz.Option> blitzOptions = new ArrayList<Blitz.Option>(4);
      if (get(FAIL_ON_ERROR)) blitzOptions.add(Blitz.Option.FAIL_ON_ERROR);
      if (get(LONGEST_MATCH)) blitzOptions.add(Blitz.Option.LONGEST_MATCH);
      if (get(SHORTEST_MATCH)) blitzOptions.add(Blitz.Option.SHORTEST_MATCH);
      if (get(MULTIPLE_MATCHES)) blitzOptions.add(Blitz.Option.MULTIPLE_MATCHES);
      if (get(SKIP_UNMATCHED_WORDS)) blitzOptions.add(Blitz.Option.SKIP_UNMATCHED_WORDS);
      return blitzOptions.toArray(new Blitz.Option[0]);
    }
  }

}
