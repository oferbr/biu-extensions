package eu.excitementproject.eop.lap.lappoc;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.factory.AggregateBuilder;

import eu.excitementproject.eop.lap.LAPAccess;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.ae.postagger.MaxentPosTaggerAE;
import eu.excitementproject.eop.lap.ae.sentencesplitter.LingPipeSentenceSplitterAE;
import eu.excitementproject.eop.lap.ae.tokenizer.MaxentTokenizerAE;

/**
 * <b>NOTE:</b> This class was adapted from eu.excitementproject.eop.lap.lappoc.OpenNLPTaggerEN<br>
 * 
 * English tokenizer + tagger, that relies on OpenNLP tagger (wrapped in DKPro component)  
 * This class is provides all LAPAccess methods simply by overriding addAnnotationTo() of LAP_ImplBase
 * 
 * @author Gil 
 *
 */
public class DemoLAP extends LAP_ImplBase implements LAPAccess {
	
	public DemoLAP() throws LAPException {
		super(); 		
		languageIdentifier = "EN"; // set languageIdentifer 
	}	

	@Override 
	public void addAnnotationOn(JCas aJCas, String viewName)
			throws LAPException 
	{
		AnalysisEngineDescription splitter = null; 
		AnalysisEngineDescription tokenizer = null; 
		AnalysisEngineDescription tagger = null; 
		try {
			splitter = createPrimitiveDescription(LingPipeSentenceSplitterAE.class);
			tokenizer = createPrimitiveDescription(MaxentTokenizerAE.class);
			tagger = createPrimitiveDescription(MaxentPosTaggerAE.class,
					MaxentPosTaggerAE.PARAM_MODEL_FILE , "src/test/resources/parser/tag.bin.gz");
		}
		catch (ResourceInitializationException re)
		{
			throw new LAPException("Failed to initilize DKPro UIMA component" ,re ); 
		}	
		// Using AggregateBuilder to assign views 
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(splitter, "_InitialView", viewName);
		builder.add(tokenizer, "_InitialView", viewName);
		builder.add(tagger, "_InitialView", viewName); 
		
		try {
			AnalysisEngine ae = builder.createAggregate(); 
			ae.process(aJCas); 
		}
		catch (ResourceInitializationException re)
		{
			throw new LAPException("Failed to initilize aggregate AE component" ,re ); 
		} 
		catch (AnalysisEngineProcessException e) 
		{
			throw new LAPException("An exception while running the aggregate AE", e); 
		}		
	}

}
