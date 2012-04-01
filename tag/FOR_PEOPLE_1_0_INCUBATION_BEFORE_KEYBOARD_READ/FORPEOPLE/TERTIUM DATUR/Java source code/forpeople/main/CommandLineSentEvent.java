package forpeople.main;

import forpeople.events.ReadEvent;

public interface CommandLineSentEvent extends ReadEvent {
	String[] getCommandLine();
}
