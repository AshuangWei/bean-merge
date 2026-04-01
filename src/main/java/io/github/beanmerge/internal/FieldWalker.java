package io.github.beanmerge.internal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Collects declared fields from a type hierarchy (subclass fields shadow superclass names).
 */
public final class FieldWalker {

  private FieldWalker() {
  }

  public static List<Field> declaredFieldsIncludingSuperclasses(Class<?> type) {
    if (type == null || type == Object.class) {
      return new ArrayList<>();
    }
    List<Field> fields = new ArrayList<>(Arrays.asList(type.getDeclaredFields()));
    Set<String> names = fields.stream().map(Field::getName).collect(Collectors.toSet());
    fields.addAll(
        declaredFieldsIncludingSuperclasses(type.getSuperclass()).stream()
            .filter(field -> !field.isSynthetic())
            .filter(field -> !names.contains(field.getName()))
            .collect(Collectors.toList()));
    return fields;
  }
}
