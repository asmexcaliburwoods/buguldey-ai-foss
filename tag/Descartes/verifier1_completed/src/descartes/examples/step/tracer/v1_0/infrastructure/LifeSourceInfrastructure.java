package descartes.examples.step.tracer.v1_0.infrastructure;

import java.io.File;

import descartes.examples.step.tracer.v1_0.DescartesParadigm.DescartesParadigm;
import descartes.examples.step.tracer.v1_0.DescartesParadigm.DescartesParadigmGrammarTranslator;

public class LifeSourceInfrastructure {
	public static DescartesParadigm parseDescartesParadigmFile(File file) {
		return DescartesParadigmGrammarTranslator.parseParadigmFile(file);
	}
}
