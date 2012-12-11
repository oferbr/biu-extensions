package eu.excitementproject.eop.lap.ae.tokenizer;

import java.io.File;

import org.uimafit.descriptor.ConfigurationParameter;

import eu.excitementproject.eop.lap.util.Envelope;

import ac.biu.nlp.nlp.instruments.tokenizer.OpenNlpTokenizer;

public class OpenNLPTokenizerAE extends TokenizerAE<OpenNlpTokenizer> {

	/**
	 * Model file of this tokenizer.
	 */
	public static final String PARAM_MODEL_FILE = "model_file";
	@ConfigurationParameter(name = PARAM_MODEL_FILE, mandatory = true)
	private String modelFile;
	
	private static Envelope<OpenNlpTokenizer> envelope = new Envelope<OpenNlpTokenizer>();
	
	@Override
	protected final Envelope<OpenNlpTokenizer> getEnvelope(){return envelope;}
	
	@Override
	protected OpenNlpTokenizer buildInnerTool() throws Exception {
		File mf = new File(modelFile);
		OpenNlpTokenizer tokenizer = new OpenNlpTokenizer(mf);
		tokenizer.init();
		return tokenizer;
	}
	
}
