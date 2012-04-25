package descartes.examples.step.tracer.v1_0;

import java.io.File;

import descartes.examples.step.tracer.v1_0.DescartesParadigm.DescartesParadigm;
import descartes.examples.step.tracer.v1_0.gui.StepTracerGUI;
import descartes.examples.step.tracer.v1_0.gui.StepTracerGUIImpl;
import descartes.examples.step.tracer.v1_0.infrastructure.LifeSourceInfrastructure;
import descartes.examples.step.tracer.v1_0.infrastructure.UncheckedRunHelper;
import descartes.examples.step.tracer.v1_0.infrastructure.UncheckedRunnable;

public class StepTracer {
	StepTracer(){
		UncheckedRunHelper.run(new UncheckedRunnable() {
			@Override
			public void run() throws Throwable {
				uncheckedLifeSource();				
			}
		});
	}

	protected void uncheckedLifeSource()throws Throwable {
		DescartesParadigm paradigm=LifeSourceInfrastructure.parseDescartesParadigmFile(
				new File("."+File.separatorChar+"paradigms"+File.separatorChar+"Descartes.descartesparadigm"));
		StepTracerGUI gui=new StepTracerGUIImpl(paradigm);  
		gui.stepTraceCOINTERPRET();
	}
}
