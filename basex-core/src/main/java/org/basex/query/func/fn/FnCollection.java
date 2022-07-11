package org.basex.query.func.fn;

import static org.basex.query.QueryError.*;

import org.basex.query.*;
import org.basex.query.value.*;
import org.basex.query.value.item.*;
import org.basex.query.value.seq.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-22, BSD License
 * @author Christian Gruen
 */
public class FnCollection extends Docs {
  @Override
  public Value value(final QueryContext qc) throws QueryException {
    return collection(qc);
  }

  @Override
  public boolean ddo() {
    return true;
  }

  /**
   * Returns a collection.
   * @param qc query context
   * @return collection
   * @throws QueryException query exception
   */
  final Value collection(final QueryContext qc) throws QueryException {
    // return default collection or parse specified collection
    QueryInput qi = queryInput;
    if(qi == null) {
      final Item item = exprs.length == 0 ? Empty.VALUE : exprs[0].atomItem(qc, info);
      if(item != Empty.VALUE) {
        qi = queryInput(toToken(item));
        if(qi == null) throw INVCOLL_X.get(info, item);
      }
    }
    return qc.resources.collection(qi, info);
  }
}
