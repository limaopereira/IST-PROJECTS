package ggc;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;

class CollatorWrapper implements Comparator<String>, Serializable {
  private static final long serialVersionUID = 202110251850L;

  private transient Collator _collator = Collator.getInstance(Locale.getDefault());

  private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
    ois.defaultReadObject();
    _collator = Collator.getInstance(Locale.getDefault());
  }

  @Override
  public int compare(String s1, String s2) {
    _collator.setStrength(Collator.SECONDARY);
    return _collator.compare(s1, s2);
  }
}
