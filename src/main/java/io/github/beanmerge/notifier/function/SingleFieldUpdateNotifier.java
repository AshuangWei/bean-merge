package io.github.beanmerge.notifier.function;

import io.github.beanmerge.notifier.UpdatedField;
import java.util.List;

public interface SingleFieldUpdateNotifier<Source, Target> extends FieldUpdateNotifier<Source, Target> {

  void updateNotify(String fieldName, Source source, Target target, Object oldValue, Object newValue);

  @Override
  default void updateNotify(Source source, Target target, List<UpdatedField> updatedFields) {
    UpdatedField updatedField = updatedFields.get(0);
    updateNotify(updatedField.getPath(), source, target, updatedField.getFrom(), updatedField.getTo());
  }
}
