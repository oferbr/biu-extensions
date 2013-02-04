package eu.excitementproject.eop.lap.ae.sentencesplitter;

import eu.excitementproject.eop.lap.biu.en.sentencesplit.MorphAdornerSentenceSplitter;
import eu.excitementproject.eop.lap.util.Envelope;

public class MorphAdornerSentenceSplitterAE extends SentenceSplitterAE<MorphAdornerSentenceSplitter> {

	private static Envelope<MorphAdornerSentenceSplitter> envelope = new Envelope<MorphAdornerSentenceSplitter>();
	
	@Override
	protected final Envelope<MorphAdornerSentenceSplitter> getEnvelope(){return envelope;}
	
	@Override
	protected MorphAdornerSentenceSplitter buildInnerTool() throws Exception {
		MorphAdornerSentenceSplitter sentenceSplitter = new MorphAdornerSentenceSplitter();
		return sentenceSplitter;
	}
	
}
