package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GAME_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_USERNAME;

import org.junit.jupiter.api.Test;

public class ArgumentMultimapTest {

    private final ArgumentMultimap argMultimap = new ArgumentMultimap();

    @Test
    public void arePrefixesPresent_noPrefixes_returnsTrue() {
        assertTrue(argMultimap.arePrefixesPresent());
    }

    @Test
    public void arePrefixesPresent_absentPrefix_returnsFalse() {
        assertFalse(argMultimap.arePrefixesPresent(PREFIX_GAME_NAME));
    }

    @Test
    public void arePrefixesPresent_allPrefixesPresent_returnsTrue() {
        argMultimap.put(PREFIX_GAME_NAME, "Minecraft");
        argMultimap.put(PREFIX_USERNAME, "player");

        assertTrue(argMultimap.arePrefixesPresent(PREFIX_GAME_NAME));
        assertTrue(argMultimap.arePrefixesPresent(PREFIX_GAME_NAME, PREFIX_USERNAME));
        assertTrue(argMultimap.arePrefixesPresent(new Prefix("g/"), new Prefix("u/")));
    }

    @Test
    public void arePrefixesPresent_somePrefixesMissing_returnsFalse() {
        argMultimap.put(PREFIX_GAME_NAME, "Minecraft");

        assertFalse(argMultimap.arePrefixesPresent(PREFIX_GAME_NAME, PREFIX_USERNAME));
        assertFalse(argMultimap.arePrefixesPresent(PREFIX_USERNAME, PREFIX_GAME_NAME));
    }

    @Test
    public void arePrefixesPresent_emptyValues_returnsTrue() {
        argMultimap.put(PREFIX_GAME_NAME, "");
        argMultimap.put(PREFIX_USERNAME, "");

        assertTrue(argMultimap.arePrefixesPresent(PREFIX_GAME_NAME, PREFIX_USERNAME));
    }

    @Test
    public void arePrefixesPresent_repeatedPrefixesWithEmptyLastValue_returnsTrue() {
        argMultimap.put(PREFIX_GAME_NAME, "Minecraft");
        argMultimap.put(PREFIX_GAME_NAME, "");

        assertTrue(argMultimap.arePrefixesPresent(PREFIX_GAME_NAME));
        assertTrue(argMultimap.arePrefixesPresent(PREFIX_GAME_NAME, PREFIX_GAME_NAME));
    }
}
