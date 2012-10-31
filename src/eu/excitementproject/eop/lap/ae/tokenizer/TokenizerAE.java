package eu.excitementproject.eop.lap.ae.tokenizer;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

import ac.biu.nlp.nlp.general.DockedToken;
import ac.biu.nlp.nlp.general.StringUtil;
import ac.biu.nlp.nlp.general.StringUtilException;
import ac.biu.nlp.nlp.instruments.tokenizer.Tokenizer;
import ac.biu.nlp.nlp.instruments.tokenizer.TokenizerException;

/**
 * A UIMA Analysis Engine that tokenizes the document in the CAS. <BR>
 * This is only a wrapper for an existing non-UIMA <code>Tokenizer</code>
 * abstract class.
 * 
 * @author Ofer Bronstein
 * @since Nov 2012
 *
 */
public class TokenizerAE extends JCasAnnotator_ImplBase {

	// TODO when do we get this instance? when this clss is instantiated?
	// Can we get params from the desc XML?
	private Tokenizer innerTokenizer;
	
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException
	{
		super.initialize(aContext);
		// TODO somehow get the non-UIMA Tokenizer object, and keep it in innerTokenizer
	}
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		try {
			Collection<Sentence> sentenceAnnotations = JCasUtil.select(aJCas, Sentence.class);
			List<String> sentenceStrings = JCasUtil.toText(sentenceAnnotations);
		
			innerTokenizer.setSentences(sentenceStrings);
			innerTokenizer.tokenize();
			List<List<String>> tokenStrings = innerTokenizer.getTokenizedSentences();
			
			if (sentenceStrings.size() != tokenStrings.size()) {
				throw new TokenizerException("Got tokenization for " + tokenStrings.size() +
						" sentences, should have gotten according to the total number of sentences: " + sentenceStrings.size());
			}
			
			for (int i=0; i<sentenceStrings.size(); i++) {
			
				String oneSentence = sentenceStrings.get(i);
				List<String> tokensOneSentence = tokenStrings.get(i);
				
				// If you get an exception for an unfound token, you can change
				// the "true" to "false", and tokens unfound in the text will be ignored  
				SortedMap<Integer, DockedToken> dockedTokens = StringUtil.getTokensOffsets(oneSentence, tokensOneSentence, true);
				
				for (DockedToken dockedToken : dockedTokens.values()) {
					Token tokenAnnot = new Token(aJCas);
					tokenAnnot.setBegin(dockedToken.getCharOffsetStart());
					tokenAnnot.setEnd(dockedToken.getCharOffsetEnd());
					tokenAnnot.addToIndexes();
				}
			}
			
		} catch (TokenizerException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		} catch (StringUtilException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		}
	}
}
