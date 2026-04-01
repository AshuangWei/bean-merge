package io.github.beanmerge.copier;

import java.lang.reflect.Field;

public abstract class Copier {

  public abstract Object copy(Field field, Object fromValue, Object toValue, String path);
}
