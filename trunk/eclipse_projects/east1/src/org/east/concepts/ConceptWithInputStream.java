package org.east.concepts;

import java.io.InputStream;
import java.io.IOException;

public interface ConceptWithInputStream{//todo HACK! must be accomplished using Action-like mechganism
  InputStream getInputStream() throws IOException;
}
