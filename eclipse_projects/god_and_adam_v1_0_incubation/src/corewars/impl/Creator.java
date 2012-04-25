package corewars.impl;

import java.io.Serializable;

import corewars.Subject;
import corewars.ABRAXAS.CreationCreature;
import corewars.ABRAXAS.Multiverse;
import corewars.ABRAXAS.WORK;

public abstract class Creator extends AbstractLivingCreature implements Subject, Serializable{
	private static final long serialVersionUID = -2782125844312501375L;
	private Logger logger;
	public Creator(Logger logger) {
		this.logger=logger;
	}
	
	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public void meditate() {
		getLogger().log(getLookerDescription(null)+" MEDITATES");
		setMeditating(true);
		try {
			long millis=5000;
			Thread.sleep(millis);
			getTiredness().rest(millis);
		} catch (InterruptedException e) {}
		setMeditating(false);
		getLogger().log(getLookerDescription(null)+" WOKE");
	}
	protected void setMeditating(boolean b) {}
	
	final protected CreationCreature prayCreationCreature(corewars.ABRAXAS.ABRAXAS_CreationCreature ABRAXAS_CreationCreature){
		ABRAXAS_CreationCreature.feel(this);
		return ABRAXAS_CreationCreature;
	}
	
	protected void create(WORK t, Multiverse multiverse){
		CoreWarsImpl corewars=(CoreWarsImpl) multiverse;
		@SuppressWarnings("unused")
		CreationCreature creativity=prayCreationCreature(corewars.prayABRAXAS().getCreationCreature());
		//TODO
	}
	public void work(WORK t) {
		create(t, t.getMultiverse());		
	}
}
