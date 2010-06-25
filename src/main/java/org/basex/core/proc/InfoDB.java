package org.basex.core.proc;

import static org.basex.core.Text.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.basex.core.CommandBuilder;
import org.basex.core.User;
import org.basex.core.Commands.Cmd;
import org.basex.core.Commands.CmdInfo;
import org.basex.data.MetaData;
import org.basex.util.Performance;
import org.basex.util.TokenBuilder;

/**
 * Evaluates the 'info database' command and returns information on the
 * currently opened database.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-10, ISC License
 * @author Christian Gruen
 */
public final class InfoDB extends AInfo {
  /** Date format. */
  private static final SimpleDateFormat DATE =
    new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

  /**
   * Default constructor.
   */
  public InfoDB() {
    super(DATAREF | User.READ);
  }

  @Override
  protected boolean run() throws IOException {
    final boolean create = context.user.perm(User.CREATE);
    out.print(db(context.data.meta, false, true, create));
    return true;
  }

  /**
   * Creates a database information string.
   * @param meta meta data
   * @param bold header bold header flag
   * @param index add index information
   * @param create create permissions
   * @return info string
   */
  public static byte[] db(final MetaData meta, final boolean bold,
      final boolean index, final boolean create) {

    final TokenBuilder tb = new TokenBuilder();
    final String header = (bold ?
        new TokenBuilder().high().add("%").norm().toString() : "%") + NL;
    tb.add(header, INFODB);
    format(tb, INFODBNAME, meta.name);
    format(tb, INFODBSIZE, Performance.format(meta.dbsize()));
    format(tb, INFONODES, Integer.toString(meta.size));
    format(tb, INFOHEIGHT, Integer.toString(meta.height));

    tb.add(NL);
    tb.add(header, INFOCREATE);
    if(create) format(tb, INFODOC, meta.file.path());
    format(tb, INFOTIME, DATE.format(new Date(meta.time)));
    format(tb, INFODOCSIZE, Performance.format(meta.filesize));
    format(tb, INFOENCODING, meta.encoding);
    format(tb, INFONDOCS, Integer.toString(meta.ndocs));
    format(tb, INFOCHOP, flag(meta.chop));
    format(tb, INFOENTITY, flag(meta.entity));

    if(index) {
      tb.add(NL);
      tb.add(header, INFOINDEX);
      if(meta.oldindex) {
        tb.add(" " + INDUPDATE + NL);
      } else {
        format(tb, INFOPATHINDEX, flag(meta.pthindex));
        format(tb, INFOTEXTINDEX, flag(meta.txtindex));
        format(tb, INFOATTRINDEX, flag(meta.atvindex));
        format(tb, INFOFTINDEX, flag(meta.ftxindex) + (meta.ftxindex &&
            meta.wildcards ? " (" + INFOWCINDEX + ")" : ""));
      }
    }
    return tb.finish();
  }

  @Override
  public void build(final CommandBuilder cb) {
    cb.init(Cmd.INFO + " " + CmdInfo.DB);
  }
}
