package org.basex.query.func.index;

import static org.basex.util.Token.*;

import org.basex.data.*;
import org.basex.index.*;
import org.basex.index.query.*;
import org.basex.query.*;
import org.basex.query.iter.*;
import org.basex.query.value.*;

/**
 * Function implementation.
 *
 * @author BaseX Team, BSD License
 * @author Christian Gruen
 */
public class IndexElementNames extends IndexFn {
  @Override
  public final Iter iter(final QueryContext qc) throws QueryException {
    final Data data = toData(qc);
    final IndexType type = type();
    return entries(type == IndexType.ELEMNAME ? data.elemNames : data.attrNames,
      new IndexEntries(EMPTY, type));
  }

  @Override
  public final Value value(final QueryContext qc) throws QueryException {
    return iter(qc).value(qc, this);
  }

  /**
   * Returns the index type (overwritten by implementing functions).
   * @return index type
   */
  IndexType type() {
    return IndexType.ELEMNAME;
  }
}
