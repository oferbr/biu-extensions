package eu.excitementproject.eop.core.eda;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnspecifiedPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetLexicalResource;
import eu.excitementproject.eop.core.component.lexicalknowledge.wordnet.WordnetRuleInfo;
import eu.excitementproject.eop.core.utilities.dictionary.wordnet.WordNetRelation;

/**
 * Test Wordnet.
 * All configuration values are given as constants, except for the path to WordNet folder itself,
 * which is a command-line argument.
 * 
 * @author Ofer Bronstein
 *
 */
public class TestWordNetLexicalResource {

	/** Test Configuration **/
	// Left Side
	private static final String L_LEMMA = "jaguar";
	private static final SimplerCanonicalPosTag L_CANONICAL_POS = SimplerCanonicalPosTag.NOUN;
	private static final Boolean L_USE_ONLY_FIRST_SENSE = true;
	
	// Right Side
	private static final String R_LEMMA = "big cat";
	private static final SimplerCanonicalPosTag R_CANONICAL_POS = SimplerCanonicalPosTag.NOUN;
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
	
	public static void main(String[] args) throws LexicalResourceException, UnsupportedPosTagStringException 
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
		
		List<LexicalRule<? extends WordnetRuleInfo>> rules;
		WordnetLexicalResource wordnetLexRes = new WordnetLexicalResource(
				wordnetDir,
				L_USE_ONLY_FIRST_SENSE,
				R_USE_ONLY_FIRST_SENSE,
				ENTAILING_RELATIONS,
				CHAINING_LENGTH,
				null);

		
		rules = wordnetLexRes.getRulesForLeft(L_LEMMA, lPos);
		System.out.println("Got " + rules.size() + " left rule(s) for: " + L_LEMMA + " (" + L_CANONICAL_POS + ") -");
		for (LexicalRule<? extends WordnetRuleInfo> rule : rules)
			System.out.println(rule);
		System.out.println("\n*****************************\n");
		
		rules = wordnetLexRes.getRulesForRight(R_LEMMA, rPos);
		System.out.println("Got " + rules.size() + " right rule(s) for: " + R_LEMMA + " (" + R_CANONICAL_POS + ") -");
		for (LexicalRule<? extends WordnetRuleInfo> rule : rules)
			System.out.println(rule);
		System.out.println("\n*****************************\n");

		rules = wordnetLexRes.getRules(L_LEMMA, lPos, R_LEMMA, rPos);
		System.out.println("Got " + rules.size() + " rule(s) between " + L_LEMMA + " (" + L_CANONICAL_POS + ") and " + R_LEMMA + " (" + R_CANONICAL_POS + ") -");
		for (LexicalRule<? extends WordnetRuleInfo> rule : rules)
			System.out.println(rule);
		System.out.println("\n*****************************\n");
	}
}
