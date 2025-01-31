package org.basex.query.func.fn;

import org.basex.query.*;
import org.basex.query.value.item.*;
import org.basex.query.value.type.*;
import org.basex.util.*;

/**
 * Function implementation.
 *
 * @author BaseX Team, BSD License
 * @author Christian Gruen
 */
public final class FnAdustDateTimeToTimezone extends DateTimeFn {
  @Override
  public Item item(final QueryContext qc, final InputInfo ii) throws QueryException {
    return adjust(AtomType.DATE_TIME, qc);
  }
}
