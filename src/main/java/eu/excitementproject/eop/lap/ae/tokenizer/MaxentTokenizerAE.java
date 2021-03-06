package eu.excitementproject.eop.lap.ae.tokenizer;

import eu.excitementproject.eop.lap.biu.en.tokenizer.MaxentTokenizer;
import eu.excitementproject.eop.lap.util.Envelope;

public class MaxentTokenizerAE extends TokenizerAE<MaxentTokenizer> {

	private static Envelope<MaxentTokenizer> envelope = new Envelope<MaxentTokenizer>();
	
	@Override
	protected final Envelope<MaxentTokenizer> getEnvelope(){return envelope;}
	
	@Override
	protected MaxentTokenizer buildInnerTool() throws Exception {
		MaxentTokenizer tokenizer = new MaxentTokenizer();
		tokenizer.init();
		return tokenizer;
	}
	
}
