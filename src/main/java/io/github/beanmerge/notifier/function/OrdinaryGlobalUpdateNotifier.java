package io.github.beanmerge.notifier.function;

import io.github.beanmerge.notifier.UpdatedField;
import java.util.List;

public interface OrdinaryGlobalUpdateNotifier<Source, Target> extends FieldUpdateNotifier<Source, Target> {

  void updateNotify();

  @Override
  default void updateNotify(Source source, Target target, List<UpdatedField> updatedFields) {
    updateNotify();
  }
}
