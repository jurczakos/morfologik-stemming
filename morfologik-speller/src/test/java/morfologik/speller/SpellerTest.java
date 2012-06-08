package morfologik.speller;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import morflogik.speller.Speller;
import morfologik.stemming.Dictionary;

import org.junit.Test;

public class SpellerTest {
	@Test
	public void testRunonWords() throws IOException {
        final URL url = getClass().getResource("slownik.dict");		
		final Speller spell = new Speller(Dictionary.read(url));
		assertTrue(spell.replaceRunOnWords("abaka").isEmpty());
		assertTrue(spell.replaceRunOnWords("abakaabace").
				contains("abaka abace"));

		// Test on an morphological dictionary - should work as well
		final URL url1 = getClass().getResource("test-infix.dict");		
		final Speller spell1 = new Speller(Dictionary.read(url1));
		assertTrue(spell1.replaceRunOnWords("Rzekunia").isEmpty());
		assertTrue(spell1.replaceRunOnWords("RzekuniaRzeczypospolitej").
				contains("Rzekunia Rzeczypospolitej"));				
	}
	
	@Test
	public void testFindReplacements() throws IOException {
		final URL url = getClass().getResource("slownik.dict");		
		final Speller spell = new Speller(Dictionary.read(url), 2);
		assertTrue(spell.findReplacements("abka").contains("abak"));
	      //check if we get only dictionary words...
		List<String> reps = spell.findReplacements("bak");
		    for (final String word: reps) {
		        assertTrue(spell.isInDictionary(word));
		    }
		assertTrue(spell.findReplacements("abka~~").isEmpty()); // 2 characters more -> edit distance too large
		assertTrue(!spell.findReplacements("Rezkunia").contains("Rzekunia"));
		
		final URL url1 = getClass().getResource("test-infix.dict");		
		final Speller spell1 = new Speller(Dictionary.read(url1));
		assertTrue(spell1.findReplacements("Rezkunia").contains("Rzekunia"));
		//diacritics
		assertTrue(spell1.findReplacements("Rzękunia").contains("Rzekunia"));
		//we should get no candidates for correct words
		assertTrue(spell1.isInDictionary("Rzekunia"));
		assertTrue(spell1.findReplacements("Rzekunia").isEmpty());
		//and no for things that are too different from the dictionary
		assertTrue(spell1.findReplacements("Strefakibica").isEmpty());
		//nothing for nothing
		assertTrue(spell1.findReplacements("").isEmpty());
	    //nothing for weird characters
        assertTrue(spell1.findReplacements("\u0000").isEmpty());
        //nothing for other characters
        assertTrue(spell1.findReplacements("«…»").isEmpty());
        //nothing for separator
        assertTrue(spell1.findReplacements("+").isEmpty());

	}	
}