package taygalove_shepherd.sachok;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PersistenceSerializableStateObject implements Serializable{
	Map<String, byte[]> records=new HashMap<String, byte[]>();
}
