package eu.excitementproject.eop.lap.ae.sentencesplitter;

import eu.excitementproject.eop.lap.biu.en.sentencesplit.LingPipeSentenceSplitter;
import eu.excitementproject.eop.lap.util.Envelope;

public class LingPipeSentenceSplitterAE extends SentenceSplitterAE<LingPipeSentenceSplitter> {

	private static Envelope<LingPipeSentenceSplitter> envelope = new Envelope<LingPipeSentenceSplitter>();
	
	@Override
	protected final Envelope<LingPipeSentenceSplitter> getEnvelope(){return envelope;}
	
	@Override
	protected LingPipeSentenceSplitter buildInnerTool() throws Exception {
		LingPipeSentenceSplitter sentenceSplitter = new LingPipeSentenceSplitter();
		return sentenceSplitter;
	}
	
}
