package io.github.beanmerge;

import io.github.beanmerge.copier.CopierFactory;
import io.github.beanmerge.copier.CustomerCopierAdapter;
import io.github.beanmerge.exception.MergeException;
import io.github.beanmerge.internal.FieldWalker;
import io.github.beanmerge.internal.PropertyPaths;
import io.github.beanmerge.notifier.NotifierManager;
import io.github.beanmerge.notifier.UpdatedNotifier;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.beanutils.PropertyUtils;

public class Merger<From, To> {

  private final CopierFactory copierFactory;
  private final Boolean ignoreNullValue;
  private final NotifierManager notifierManager;
  private final Set<Class> customImmutableTypes;
  private final From source;
  private final To target;

  public Merger(Map<Class, Set<CustomerCopierAdapter>> customs, List<UpdatedNotifier> notifiers,
      Boolean ignoreNullValue, Set<Class> customImmutableTypes, From source, To target) {
    this.copierFactory = new CopierFactory(this, customs);
    this.ignoreNullValue = ignoreNullValue;
    this.customImmutableTypes = customImmutableTypes;
    this.notifierManager = new NotifierManager(notifiers);
    this.source = source;
    this.target = target;
  }

  public boolean merge() {
    boolean result = merge(source, target, "");
    sendNotify();
    return result;
  }

  public <Source, Target> boolean merge(Source from, Target to, String path) {
    boolean hasChange = FieldWalker.declaredFieldsIncludingSuperclasses(to.getClass())
        .stream()
        .filter(field -> isWritableAndReadable(from, to, field))
        .map(field -> updateField(from, to, field, path))
        .collect(Collectors.toList()).stream().anyMatch(Boolean::booleanValue);
    if (hasChange) {
      notifierManager.addUpdatedPath(path, from, to);
    }
    return hasChange;
  }

  public Set<Class> getCustomImmutableTypes() {
    return customImmutableTypes;
  }

  private void sendNotify() {
    notifierManager.notifyUpdate(source, target);
  }

  private <Source, Target> boolean updateField(Source from, Target to, Field field, String path) {
    try {
      Object fromValue = PropertyUtils.getSimpleProperty(from, field.getName());
      if (!(ignoreNullValue && fromValue == null)) {
        Object originToValue = PropertyUtils.getSimpleProperty(to, field.getName());
        Object toValue = copierFactory.getCopier(field.getType(), fromValue).copy(field, fromValue, originToValue, path);
        if (!valuesEqual(toValue, originToValue)) {
          PropertyUtils.setSimpleProperty(to, field.getName(), toValue);
          String fieldPath = PropertyPaths.childPath(path, field.getName());
          notifierManager.addUpdatedPath(fieldPath, originToValue, toValue);
          return true;
        }
      }
      return false;
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new MergeException(e);
    }
  }

  private <Source, Target> boolean isWritableAndReadable(Source from, Target to, Field field) {
    return PropertyUtils.isWriteable(to, field.getName()) && PropertyUtils.isReadable(from, field.getName());
  }

  private boolean valuesEqual(Object toValue, Object originToValue) {
    if (toValue == null) {
      return originToValue == null;
    } else if (Comparable.class.isInstance(toValue) && Comparable.class.isInstance(originToValue)) {
      return ((Comparable) toValue).compareTo(originToValue) == 0;
    } else {
      return Objects.equals(toValue, originToValue);
    }
  }
}
