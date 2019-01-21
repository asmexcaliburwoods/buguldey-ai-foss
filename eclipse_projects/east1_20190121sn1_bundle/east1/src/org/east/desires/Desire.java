package org.east.desires;

import org.east.reasons.popupDesireReasons.DesireReason;

public interface Desire{
  DesireReason getDesireReason();
  boolean isEstimatedAsReachable();
  boolean isEstimatedAsUnreachable();
  boolean isFulfilled();
  void stepToCompletion();//todo replace this by deliberation
  boolean shouldNowRunStepToCompletion();
}
