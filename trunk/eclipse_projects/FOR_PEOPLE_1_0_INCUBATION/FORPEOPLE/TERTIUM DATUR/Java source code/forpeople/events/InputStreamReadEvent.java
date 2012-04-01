package forpeople.events;

import java.io.InputStream;


public interface InputStreamReadEvent extends ReadEvent{
	InputStream getSourceInputStream();
	byte[] getByteBuffer();
	int getStartByteOffset();
	int getByteLength();
}
