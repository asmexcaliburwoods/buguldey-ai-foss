package corewars.impl;

import corewars.Trajectory;

public class TriangleTrajectoryImpl implements Trajectory {
	private boolean forth;

	public TriangleTrajectoryImpl(boolean forth) {
		super();
		this.forth = forth;
	}

	public boolean isForth() {
		return forth;
	}

	@Override
	public String getLookedAtDescription() {
		return forth?"GO FORTH":"GO BACK";
	}
}
