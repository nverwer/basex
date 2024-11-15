package org.basex.query.func.map;

import org.basex.query.*;
import org.basex.query.expr.*;
import org.basex.query.func.*;
import org.basex.query.iter.*;
import org.basex.query.value.*;
import org.basex.query.value.item.*;
import org.basex.query.value.map.*;
import org.basex.query.value.type.*;
import org.basex.util.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-24, BSD License
 * @author Christian Gruen
 */
public final class MapOfPairs extends StandardFunc {
  @Override
  public XQMap item(final QueryContext qc, final InputInfo ii) throws QueryException {
    final Iter input = arg(0).iter(qc);
    final FItem combine = toFunctionOrNull(arg(1), 2, qc);

    final HofArgs cargs = combine != null ? new HofArgs(2) : null;
    XQMap result = XQMap.empty();
    for(Item item; (item = qc.next(input)) != null;) {
      // extract key/value record entries

      final XQMap map = toRecord(item, RecordType.PAIR, qc);
      final Item key = toAtomItem(map.get(Str.KEY), qc);
      Value value = map.get(Str.VALUE);
      if(result.contains(key)) {
        final Value old = result.get(key);
        value = combine != null ? invoke(combine, cargs.set(0, old).set(1, value), qc) :
          ValueBuilder.concat(old, value, qc);
      }
      result = result.put(key, value);
    }
    return result;
  }

  @Override
  protected Expr opt(final CompileContext cc) {
    final Type tp = arg(0).seqType().type;
    if(tp instanceof MapType) {
      final SeqType vt = ((MapType) tp).valueType;
      final AtomType kt = vt.type.atomic();
      exprType.assign(MapType.get(kt != null ? kt : AtomType.ANY_ATOMIC_TYPE, vt));
    }
    return this;
  }

  @Override
  public int hofIndex() {
    return 1;
  }
}
