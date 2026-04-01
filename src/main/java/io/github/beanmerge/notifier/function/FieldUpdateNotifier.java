package io.github.beanmerge.notifier.function;

import io.github.beanmerge.notifier.UpdatedField;
import java.util.List;

public interface FieldUpdateNotifier<Source, Target> {

  void updateNotify(Source source, Target target, List<UpdatedField> updatedFields);
}
