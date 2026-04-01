package io.github.beanmerge.notifier.function;

import io.github.beanmerge.notifier.UpdatedField;
import java.util.List;

public interface GlobalUpdateNotifier<Source, Target> extends FieldUpdateNotifier<Source, Target> {

  void updateNotify(Source source, Target target);

  @Override
  default void updateNotify(Source source, Target target, List<UpdatedField> updatedFields) {
    updateNotify(source, target);
  }
}
