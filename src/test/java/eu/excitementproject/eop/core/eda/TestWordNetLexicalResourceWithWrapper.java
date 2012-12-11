package eu.excitementproject.eop.core.eda;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ac.biu.nlp.nlp.instruments.dictionary.wordnet.WordNetRelation;
import ac.biu.nlp.nlp.lexical_resource.impl.wordnet.WordnetLexicalResource;
import ac.biu.nlp.nlp.lexical_resource.impl.wordnet.WordnetRuleInfo;
import ac.biu.nlp.nlp.lexical_resource.wrap.LexicalResourceWrapper;
import ac.biu.nlp.nlp.lexical_resource.wrap.UnspecifiedPartOfSpeech;

import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.core.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.core.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.core.representation.parsetree.CanonicalPosTag;
import eu.excitementproject.eop.core.representation.parsetree.PartOfSpeech;
import eu.excitementproject.eop.core.representation.parsetree.UnsupportedPosTagStringException;

//import ac.biu.nlp.nlp.instruments.dictionary.wordnet.WordNetRelation;
//import ac.biu.nlp.nlp.lexical_resource.LexicalResourceException;
//import ac.biu.nlp.nlp.lexical_resource.LexicalRule;
//import ac.biu.nlp.nlp.lexical_resource.impl.wordnet.WordnetLexicalResource;
//import ac.biu.nlp.nlp.lexical_resource.impl.wordnet.WordnetRuleInfo;
//import ac.biu.nlp.nlp.representation.CanonicalPosTag;
//import ac.biu.nlp.nlp.representation.PartOfSpeech;
//import ac.biu.nlp.nlp.representation.UnspecifiedPartOfSpeech;
//import ac.biu.nlp.nlp.representation.UnsupportedPosTagStringException;

/**
 * Test Wordnet <b>using the EOP interface</b> (with the wrapper).
 * All configuration values are given as constants, except for the path to WordNet folder itself,
 * which is a command-line argument.
 * 
 * @author Ofer Bronstein
 *
 */
public class TestWordNetLexicalResourceWithWrapper {

	/** Test Configuration **/
	// Left Side
	private static final String L_LEMMA = "jaguar";
	private static final CanonicalPosTag L_CANONICAL_POS = CanonicalPosTag.N;
	private static final Boolean L_USE_ONLY_FIRST_SENSE = true;
	
	// Right Side
	private static final String R_LEMMA = "feline";
	private static final CanonicalPosTag R_CANONICAL_POS = CanonicalPosTag.N;
	private static final Boolean R_USE_ONLY_FIRST_SENSE = true;
	
	// General
	private static final int CHAINING_LENGTH = 2;
	private static final Set<WordNetRelation> ENTAILING_RELATIONS = new HashSet<WordNetRelation>(Arrays.asList(new WordNetRelation[] {
			WordNetRelation.HYPERNYM,
			WordNetRelation.INSTANCE_HYPERNYM,
			WordNetRelation.MEMBER_HOLONYM,
			WordNetRelation.PART_HOLONYM,
			WordNetRelation.ENTAILMENT,
			WordNetRelation.SUBSTANCE_MERONYM, 
			//WordNetRelation.DERIVED, //technically DERIVED is also entailing, but it is not implemented in the current wrapper 
	}));
	
	public static void main(String[] args)  throws LexicalResourceException, UnsupportedPosTagStringException, ac.biu.nlp.nlp.lexical_resource.LexicalResourceException
	{
		System.out.println("WordNet test - Start \n*****************************\n");
		if (args.length != 1)
		{
			System.err.println("Path to WordNet folder should be provided as argument");
			return;
		}
		File wordnetDir = new File(args[0]);
		
		PartOfSpeech lPos = new UnspecifiedPartOfSpeech(L_CANONICAL_POS);
		PartOfSpeech rPos = new UnspecifiedPartOfSpeech(R_CANONICAL_POS);
		
		System.out.println("Configuration:\n\tWordNet Folder: " + wordnetDir + 
				"\n\tEntailment Relations: " + ENTAILING_RELATIONS + 
				"\n\tChaining Length: " + CHAINING_LENGTH + 
				"\n\tLeft:" +
				"\n\t\tLemma: " + L_LEMMA +
				"\n\t\tPOS: " + L_CANONICAL_POS +
				"\n\t\tUse Only First Sense: " + L_USE_ONLY_FIRST_SENSE +
				"\n\tRight:" + 
				"\n\t\tLemma: " + R_LEMMA +
				"\n\t\tPOS: " + R_CANONICAL_POS +
				"\n\t\tUse Only First Sense: " + R_USE_ONLY_FIRST_SENSE +
				"\n*****************************\n");
		
		List<? extends LexicalRule<? extends RuleInfo>> rules;
		WordnetLexicalResource wordnetLexRes = new WordnetLexicalResource(
				wordnetDir,
				L_USE_ONLY_FIRST_SENSE,
				R_USE_ONLY_FIRST_SENSE,
				ENTAILING_RELATIONS,
				CHAINING_LENGTH,
				null);
		
		// Here is the catch - you build a BIU lexical resource, and then wrap it with this wrapper,
		// which implements the EOP interface
		LexicalResource<? extends RuleInfo> wrapperLexRes = new LexicalResourceWrapper<WordnetRuleInfo>(wordnetLexRes);

		
		rules = wrapperLexRes.getRulesForLeft(L_LEMMA, lPos);
		System.out.println("Got " + rules.size() + " left rule(s) for: " + L_LEMMA + " (" + L_CANONICAL_POS + ") -");
		for (LexicalRule<? extends RuleInfo> rule : rules)
			System.out.println(rule);
		System.out.println("\n*****************************\n");
		
		rules = wrapperLexRes.getRulesForRight(R_LEMMA, rPos);
		System.out.println("Got " + rules.size() + " right rule(s) for: " + R_LEMMA + " (" + R_CANONICAL_POS + ") -");
		for (LexicalRule<? extends RuleInfo> rule : rules)
			System.out.println(rule);
		System.out.println("\n*****************************\n");

		rules = wrapperLexRes.getRules(L_LEMMA, lPos, R_LEMMA, rPos);
		System.out.println("Got " + rules.size() + " rule(s) between " + L_LEMMA + " (" + L_CANONICAL_POS + ") and " + R_LEMMA + " (" + R_CANONICAL_POS + ") -");
		for (LexicalRule<? extends RuleInfo> rule : rules)
			System.out.println(rule);
		System.out.println("\n*****************************\n");
	}
}
