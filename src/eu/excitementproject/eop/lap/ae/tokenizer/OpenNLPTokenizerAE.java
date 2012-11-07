package eu.excitementproject.eop.lap.ae.tokenizer;

import java.io.File;

import org.uimafit.descriptor.ConfigurationParameter;

import ac.biu.nlp.nlp.instruments.tokenizer.OpenNlpTokenizer;
import ac.biu.nlp.nlp.instruments.tokenizer.Tokenizer;

public class OpenNLPTokenizerAE extends TokenizerAE {

	/**
	 * Model file of this tokenizer.
	 */
	public static final String PARAM_MODEL_FILE = "model_file";
	@ConfigurationParameter(name = PARAM_MODEL_FILE, mandatory = true)
	private String modelFile;
	
	@Override
	protected Tokenizer getInnerTool() throws Exception {
		return new OpenNlpTokenizer(new File(modelFile));
	}

}
