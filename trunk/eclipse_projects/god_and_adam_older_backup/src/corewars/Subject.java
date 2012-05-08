package corewars;

public interface Subject{
	Will getWill();

	String getLookerDescription(Subject investigator);

	CoreWars getCoreWars();
	void setCoreWars(CoreWars corewars);

	boolean evaluate(Trajectory trajectory);

	void meditate();
}
