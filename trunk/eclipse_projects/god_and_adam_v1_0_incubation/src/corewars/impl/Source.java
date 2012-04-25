package corewars.impl;

import javax.swing.JApplet;

import corewars.AIRobotVisVitalis;
import corewars.HomoSapiensSapiensInterface;
import corewars.Situation;
import corewars.SituationVisualiser;
import corewars.Subject;

public class Source extends JApplet{
	private static final long serialVersionUID = 991326846938138323L;
	public void start(){
	  try{
		  main(new String[]{});
	  }catch(Throwable tr){
		  tr.printStackTrace();
	  }
	}
  public static void main(String[] args)throws Throwable{
	boolean consoleLogger=args.length>0&&args[0].equals("--console");
    CoreWarsImpl corewars=new CoreWarsImpl();
	WorldVisualiser_Swing mainvis=consoleLogger?null:new WorldVisualiser_Swing(corewars);
    Logger logger=consoleLogger?new LoggerImpl_Console(corewars):mainvis.getLogger();
	corewars.setLogger(logger);
    //GodImpl is controlled by user's input
    HomoSapiensSapiensInterface god=new HomoSapiensSapiensInterfaceImpl(logger);
    god.setCoreWars(corewars);
    Subject adam=new HumanisticAIRobot("HUMANISTIC AI ROBOT",new AIRobotVisVitalis() {},logger);
    adam.setCoreWars(corewars);
    SituationVisualiser svgod=consoleLogger?new TriangleSituationVisualiserImpl_Console(corewars,logger):new TriangleSituationVisualiserImpl_Swing(mainvis);
    Situation currentSituationOfGod=corewars.getCurrentSituation(god);
    svgod.set(currentSituationOfGod);
    if(mainvis!=null)mainvis.add((TriangleSituationVisualiserImpl_Swing)svgod);
    SituationVisualiser svadam=consoleLogger?new TriangleSituationVisualiserImpl_Console(corewars,logger):new TriangleSituationVisualiserImpl_Swing(mainvis);
    Situation currentSituationOfAdam=corewars.getCurrentSituation(adam);
    svadam.set(currentSituationOfAdam);
    if(mainvis!=null)mainvis.add((TriangleSituationVisualiserImpl_Swing)svadam);
    //if(mainvis!=null)mainvis.setVisible(true);
    svgod.eternityStart();
    svadam.eternityStart();
    corewars.eternity();
    svadam.eternityEnd();
    svgod.eternityEnd();
  }
}
