package seedu.address.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.ParserUtil;
import seedu.address.model.game.meta.Meta;
import seedu.address.testutil.GameBuilder;

public class GameMatchesMetadataPredicateTest {
    @Test
    public void test_allPairsOnSameGame_keysIgnoreCaseAndValuesPreserveCase() throws Exception {
        Game game = new GameBuilder().withGameName("Valorant")
                .addMeta("Role", "Healer").addMeta("Region", "Asia:SG").build();
        assertTrue(predicate("valorant", "role:Healer", "REGION:Asia:SG").test(game));
        assertFalse(predicate("Val", "Role:Healer").test(game));
        assertFalse(predicate("Valorant", "Role:healer").test(game));
        assertFalse(predicate("Valorant", "Role:Healer", "Level:30").test(game));
        assertFalse(predicate("Valorant", "Role:Healer", "Region:Europe").test(game));
        assertTrue(predicate("Valorant").test(new GameBuilder().withGameName("VALORANT").build()));
    }

    @Test
    public void constructor_copiesConditions() throws Exception {
        Set<Meta> source = new HashSet<>(ParserUtil.parseMetas(Set.of("Role:Healer")));
        GameMatchesMetadataPredicate predicate = new GameMatchesMetadataPredicate(new GameName("Valorant"), source);
        source.clear();
        assertFalse(predicate.test(new GameBuilder().withGameName("Valorant").build()));
    }

    @Test
    public void equalsAndHashCode_followGameAndMetadataIdentity() throws Exception {
        GameMatchesMetadataPredicate first = predicate("Valorant", "Role:Healer", "Region:Asia");
        GameMatchesMetadataPredicate equivalent = predicate("VALORANT", "region:Asia", "ROLE:Healer");
        assertEquals(first, first);
        assertEquals(first, equivalent);
        assertEquals(first.hashCode(), equivalent.hashCode());
        assertNotEquals(first, null);
        assertNotEquals(first, "Valorant");
        assertNotEquals(first, predicate("Minecraft", "Role:Healer", "Region:Asia"));
        assertNotEquals(first, predicate("Valorant", "Role:healer", "Region:Asia"));
        assertNotEquals(first, predicate("Valorant", "Role:Healer"));
    }

    @Test
    public void toStringMethod() throws Exception {
        GameMatchesMetadataPredicate predicate = predicate("Valorant", "Role:Healer");
        String expected = GameMatchesMetadataPredicate.class.getCanonicalName()
                + "{gameName=Valorant, conditions=[Role:Healer]}";
        assertEquals(expected, predicate.toString());
    }

    private GameMatchesMetadataPredicate predicate(String game, String... pairs) throws Exception {
        return new GameMatchesMetadataPredicate(new GameName(game), ParserUtil.parseMetas(Set.of(pairs)));
    }
}
