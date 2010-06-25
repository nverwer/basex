package org.basex.core.proc;

import java.io.IOException;

import org.basex.core.CommandBuilder;
import org.basex.core.Proc;
import org.basex.core.User;
import org.basex.core.Commands.Cmd;
import org.basex.core.Commands.CmdShow;

/**
 * Evaluates the 'show sessions' command and shows server sessions.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-10, ISC License
 * @author Christian Gruen
 */
public final class ShowSessions extends Proc {
  /**
   * Default constructor.
   */
  public ShowSessions() {
    super(User.ADMIN);
  }

  @Override
  protected boolean run() throws IOException {
    out.println(context.sessions.info());
    return true;
  }

  @Override
  public void build(final CommandBuilder cb) {
    cb.init(Cmd.SHOW + " " + CmdShow.SESSIONS);
  }
}
