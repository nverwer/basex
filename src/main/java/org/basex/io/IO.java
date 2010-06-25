package org.basex.io;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.basex.core.Main;
import org.basex.util.Token;
import org.basex.util.TokenBuilder;
import org.xml.sax.InputSource;

/**
 * Generic representation for inputs and outputs. The underlying source can
 * be a local file ({@link IOFile}), a URL ({@link IOUrl}) or a byte array
 * ({@link IOContent}).
 *
 * @author Workgroup DBIS, University of Konstanz 2005-10, ISC License
 * @author Christian Gruen
 */
public abstract class IO {
  /** Database file suffix. */
  public static final String BASEXSUFFIX = ".basex";
  /** XQuery file suffix. */
  public static final String XQSUFFIX = ".xq";
  /** XML file suffix. */
  public static final String XMLSUFFIX = ".xml";
  /** ZIP file suffix. */
  public static final String ZIPSUFFIX = ".zip";
  /** GZIP file suffix. */
  public static final String GZSUFFIX = ".gz";

  /** Disk block/page size. */
  public static final int BLOCKSIZE = 1 << 12;
  /** Table node size power. */
  public static final int NODEPOWER = 4;
  /** Maximum supported tree height. */
  public static final int MAXHEIGHT = 1 << 8;
  /** Offset for inlining numbers. */
  public static final long NUMOFF = 0x8000000000L;

  /** File path and name. */
  protected String path;
  /** File contents. */
  protected byte[] cont;
  /** First call. */
  protected boolean more;
  /** File name. */
  private String name;

  /**
   * Protected constructor.
   * @param p path
   */
  protected IO(final String p) {
    init(p);
  }

  /**
   * Sets the file path and name.
   * @param p file path
   */
  protected final void init(final String p) {
    path = p;
    // use timer if no name is given
    final String n = path.substring(path.lastIndexOf('/') + 1);
    name = n.isEmpty() ? Long.toString(System.currentTimeMillis()) : n;
  }

  /**
   * Constructor.
   * @param s source
   * @return IO reference
   */
  public static IO get(final String s) {
    if(s == null) return new IOFile("");
    if(s.startsWith("<")) return new IOContent(Token.token(s));
    if(!s.contains(":") || s.startsWith("file:") ||
        s.length() > 2 && s.charAt(1) == ':') return new IOFile(s);
    return new IOUrl(s);
  }

  /**
   * Returns the contents.
   * @return contents
   * @throws IOException I/O exception
   */
  public final byte[] content() throws IOException {
    if(cont == null) cache();
    return cont;
  }

  /**
   * Caches the contents.
   * @throws IOException I/O exception
   */
  public abstract void cache() throws IOException;

  /**
   * Tests if the file exists.
   * @return result of check
   */
  public boolean exists() {
    return true;
  }

  /**
   * Tests if this is a directory instance.
   * @return result of check
   */
  public boolean isDir() {
    return false;
  }

  /**
   * Returns the directory of this path.
   * @return result of check
   */
  public final String getDir() {
    return isDir() ? path() : path.substring(0, path.lastIndexOf('/') + 1);
  }

  /**
   * Returns the modification date of this file.
   * @return modification date
   */
  public long date() {
    return System.currentTimeMillis();
  }

  /**
   * Returns the file length.
   * @return file length
   */
  public long length() {
    return cont != null ? cont.length : 0;
  }

  /**
   * Checks if more input streams are found.
   * @return result of check
   * @throws IOException I/O exception
   */
  @SuppressWarnings("unused")
  public boolean more() throws IOException {
    return more ^= true;
  }

  /**
   * Returns the next input source.
   * @return input source
   */
  public abstract InputSource inputSource();

  /**
   * Returns a buffered reader for the input.
   * @return buffered reader
   * @throws IOException I/O exception
   */
  public abstract BufferInput buffer() throws IOException;

  /**
   * Merges two filenames.
   * @param fn file name/path to be merged
   * @return contents
   */
  public abstract IO merge(final String fn);

  /**
   * Creates the directory.
   * @return contents
   */
  public boolean md() {
    Main.notexpected();
    return false;
  }

  /**
   * Chops the path and the XML suffix of the specified filename
   * and returns the database name.
   * @return database name
   */
  public final String dbname() {
    final String n = name();
    final int i = n.lastIndexOf(".");
    return (i != -1 ? n.substring(0, i) : n).replaceAll("[^\\w.-]", "");
  }

  /**
   * Chops the path of the specified filename. If no name can be extracted,
   * the current number of milliseconds is used as name.
   * @return file name
   */
  public final String name() {
    return name;
  }

  /**
   * Returns the path.
   * @return path
   */
  public final String path() {
    return path;
  }

  /**
   * Creates a URL from the specified path.
   * @return URL
   */
  public String url() {
    return path;
  }

  /**
   * Returns the directory.
   * @return chopped filename
   */
  public String dir() {
    Main.notexpected();
    return null;
  }

  /**
   * Returns the children of a document.
   * @return chopped filename
   */
  public IO[] children() {
    Main.notexpected();
    return null;
  }

  /**
   * Writes the specified file contents.
   * @param c contents
   * @throws IOException I/O exception
   */
  @SuppressWarnings("unused")
  public void write(final byte[] c) throws IOException {
    Main.notexpected();
  }

  /**
   * Chops the path and the XML suffix of the specified filename.
   * @return chopped filename
   */
  public boolean delete() {
    Main.notexpected();
    return false;
  }

  /**
   * Compares the filename of the specified IO reference.
   * @param io io reference
   * @return result of check
   */
  public final boolean eq(final IO io) {
    return path.equals(io.path);
  }

  @Override
  public String toString() {
    return path;
  }

  /**
   * Caches the contents of the specified input stream.
   * @param i input stream
   * @return cached contents
   * @throws IOException I/O exception
   */
  protected final byte[] cache(final InputStream i) throws IOException {
    final TokenBuilder tb = new TokenBuilder();
    final InputStream bis = i instanceof BufferedInputStream ? i :
      new BufferedInputStream(i);
    int b;
    while((b = bis.read()) != -1)
      tb.add((byte) b);
    bis.close();
    cont = tb.finish();
    return cont;
  }
}
