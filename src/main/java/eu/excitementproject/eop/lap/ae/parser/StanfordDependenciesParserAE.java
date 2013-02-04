package eu.excitementproject.eop.lap.ae.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicConstructionNode;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech;
import eu.excitementproject.eop.lap.ae.SingletonSynchronizedAnnotator;
import eu.excitementproject.eop.lap.biu.en.parser.BasicPipelinedParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggedToken;


/**
 * A UIMA Analysis Engine that parses the document in the CAS for a dependency parse. <BR>
 * This is only a wrapper for an existing non-UIMA <code>eu.excitementproject.eop.lap.biu.postagger.PosTagger</code>
 * interface.
 * 
 * @author Ofer Bronstein
 * @since Jan 2013
 *
 */
public abstract class StanfordDependenciesParserAE<T extends BasicPipelinedParser> extends SingletonSynchronizedAnnotator<T> {

	protected MappingProvider mappingProvider;
	
	@Override
	public void initialize(UimaContext aContext)
		throws ResourceInitializationException
	{
		super.initialize(aContext);

		mappingProvider = new MappingProvider();
		mappingProvider.setDefault(MappingProvider.BASE_TYPE, POS.class.getName());
		configureMapping();
	}
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			//CAS cas = jcas.getCas();
			//mappingProvider.configure(cas);
			
			Collection<Sentence> sentenceAnnotations = JCasUtil.select(jcas, Sentence.class);
			List<List<POS>> posAnnotations = new ArrayList<List<POS>>(sentenceAnnotations.size());
			//List<List<String>> tokenStrings = new ArrayList<List<String>>(sentenceAnnotations.size());

			List<List<PosTaggedToken>> taggedTokens = new ArrayList<List<PosTaggedToken>>(sentenceAnnotations.size());

			for (Sentence sentenceAnnotation : sentenceAnnotations) {
				List<POS> posAnnotationsOnesentence = JCasUtil.selectCovered(jcas, POS.class, sentenceAnnotation);
				posAnnotations.add(posAnnotationsOnesentence);
				List<PosTaggedToken> taggedTokensOneSentence = new ArrayList<PosTaggedToken>(posAnnotationsOnesentence.size());
				taggedTokens.add(taggedTokensOneSentence);
				for (POS posAnnotation : posAnnotationsOnesentence) {
					String tokenText = posAnnotation.getCoveredText();
					PennPartOfSpeech partOfSpeech = new PennPartOfSpeech(posAnnotation.getPosValue());
					PosTaggedToken taggedToken = new PosTaggedToken(tokenText, partOfSpeech);
					taggedTokensOneSentence.add(taggedToken);
					
				}
				
//				tokenAnnotations.add(tokenAnnotationsOnesentence);
//				List<String> tokenStringsOneSentence = JCasUtil.toText(tokenAnnotationsOnesentence);
//				tokenStrings.add(tokenStringsOneSentence);
			}
			
			List<List<BasicConstructionNode>> noseLists = new ArrayList<List<BasicConstructionNode>>(sentenceAnnotations.size());

			
			// Using the inner tool
			// This is done in a different for-loop, to avoid entering the synchornized() block
			// many times (once per Sentence)
			synchronized (innerTool) {
				for (List<PosTaggedToken> taggedTokensOneSentence : taggedTokens) {
					innerTool.setSentence(taggedTokensOneSentence);
					innerTool.parse();
					List<BasicConstructionNode> nodeListOneSentence = innerTool.getNodesOrderedByWords();
					noseLists.add(nodeListOneSentence);
				}
			}
			
			// Process each sentence
			Iterator<List<POS>> iterPosAnnotations = posAnnotations.iterator();
			Iterator<List<BasicConstructionNode>> iterNodeLists = noseLists.iterator();
			while (iterPosAnnotations.hasNext()) {
				List<POS> posAnnotationsOnesentence = iterPosAnnotations.next();
				List<BasicConstructionNode> nodeListOneSentence = iterNodeLists.next();
				
				if (posAnnotationsOnesentence.size() != nodeListOneSentence.size()) {
					throw new ParserRunException("Got parse for " + nodeListOneSentence.size() +
							" tokens, should have gotten according to the total number of tokens in the sentence: " + posAnnotationsOnesentence.size());
				}
								
				// Process each token
				Iterator<POS> iterPosAnnotationsOnesentence = posAnnotationsOnesentence.iterator();
				Iterator<BasicConstructionNode> iterNodeListOneSentence = nodeListOneSentence.iterator();
				while (iterPosAnnotationsOnesentence.hasNext()) {
					POS posAnnotation = iterPosAnnotationsOnesentence.next();
					//String tagString = taggedToken.getPartOfSpeech().getStringRepresentation();
					BasicConstructionNode node = iterNodeListOneSentence.next();
					NodeInfo nodeInfo = node.getInfo().getNodeInfo();
					EdgeInfo edgeInfo = node.getInfo().getEdgeInfo();
					
					// validate word text
					String tokenText = posAnnotation.getCoveredText();
					if (tokenText != nodeInfo.getWord()) {
						throw new ParserRunException("For token covering text \"" + tokenText + "\", got from parse a node " +
									"with text \"" + nodeInfo.getWord() + "\" (serial=" + nodeInfo.getSerial() + ")");
					}
					
					// handle Lemma
					Lemma lemma = new Lemma(jcas, token.getBegin(), token.getEnd());
					lemma.setValue(nodeInfo.getWordLemma());
					lemma.addToIndexes();
					token.setLemma(lemma);
					
					// handle Dependency
					//////////////////////////////////////////////////////
					// Taken from de.tudarmstadt.ukp.dkpro.core.stanfordnlp.util.StanfordAnnotator.createDependencyAnnotation()
					String dependencyType = aGramRel.getShortName();

					if (dependencyType.equalsIgnoreCase("AUX")) {
						dependencyType = "AUX0";
					}

					// create the necessary objects and methods
					String dependencyTypeName = DEPPACKAGE + dependencyType.toUpperCase();

					Type type = jCas.getTypeSystem().getType(dependencyTypeName);
			        if (type == null) {
						throw new IllegalStateException("Type [" + dependencyTypeName + "] mapped to tag ["
								+ dependencyType + "] is not defined in type system");
			        }
					
					AnnotationFS anno = jCas.getCas().createAnnotation(type, aBegin, aEnd);
					anno.setStringValue(type.getFeatureByBaseName("DependencyType"), aGramRel.toString());
					anno.setFeatureValue(type.getFeatureByBaseName("Governor"), aGovernor);
					anno.setFeatureValue(type.getFeatureByBaseName("Dependent"), aDependent);

					jCas.addFsToIndexes(anno);
					//////////////////////////////////////////////////////////////
					
					
					
					
					// Get an annotation with the appropriate UIMA type via the mappingProvider
					Type posTag = mappingProvider.getTagType(tagString);
					POS posAnnotation = (POS) cas.createAnnotation(posTag, tokenAnnotation.getBegin(), tokenAnnotation.getEnd());
					posAnnotation.setPosValue(tagString);
					posAnnotation.addToIndexes();
					
					tokenAnnotation.setPos(posAnnotation);
				}
			}
		} catch (ParserRunException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		}
	}
	
	/**
	 * Allow the subclass to provide details regarding its MappingProvider.
	 */
	protected abstract void configureMapping();
}
