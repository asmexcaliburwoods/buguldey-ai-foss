package org.east.goals;

import org.east.desires.Desire;
import org.east.reasons.popupGoalReasons.GoalReason;

public interface Goal extends Desire{
  GoalReason getGoalReason();
  boolean isReached();
}
