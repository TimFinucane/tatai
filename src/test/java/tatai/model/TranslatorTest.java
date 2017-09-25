package tatai.model;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the Translator for correctness. These are just simple tests
 * that only check the core expected functionality. We shouldn't have
 * to test out of bounds as this class should never recieve out of
 * bounds results
 */
public class TranslatorTest {

    @Test
    public void testSingleDigit() {
        assertEquals("tahi", Translator.convert(1));

        assertEquals("iwa", Translator.convert(9));
    }

    @Test
    public void testTen() {
        assertEquals("tekau", Translator.convert(10));
    }

    @Test
    public void testHighTens() {
        assertEquals("rua tekau", Translator.convert(20));
        assertEquals("iwa tekau", Translator.convert(90));
    }

    @Test
    public void testMultiDigit() {
        assertEquals("tekau m\u0101 tahi", Translator.convert(11));
        assertEquals("toru tekau m\u0101 rima", Translator.convert(35));
        assertEquals("waru tekau m\u0101 waru", Translator.convert(88));
    }
}