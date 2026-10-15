package seedu.address.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.game.meta.Meta;
import seedu.address.model.game.meta.MetaKey;
import seedu.address.model.game.meta.MetaValue;
import seedu.address.testutil.GameBuilder;

public class GameTest {

    private static final GameName VALORANT = new GameName("Valorant");
    private static final GameName MINECRAFT = new GameName("Minecraft");
    private static final Username SHADOW_STRIKER = new Username("ShadowStrikerXx");
    private static final Username PIXEL_MINER = new Username("PixelMiner");

    @Test
    public void constructor_metadata_copiesAndProtectsCollection() {
        Meta role = new Meta(new MetaKey("Role"), new MetaValue("Support"));
        List<Meta> source = new ArrayList<>(List.of(role));
        Game game = new Game(VALORANT, SHADOW_STRIKER, source);
        source.clear();
        assertEquals(Set.of(role), game.getMetas());
        assertThrows(UnsupportedOperationException.class, () -> game.getMetas().clear());
        assertEquals(Set.of(), new Game(VALORANT, SHADOW_STRIKER).getMetas());
        assertEquals(game, new GameBuilder(game).build());
    }

    @Test
    public void constructor_invalidMetadata_rejected() {
        Meta role = new Meta(new MetaKey("Role"), new MetaValue("Support"));
        assertThrows(NullPointerException.class, () -> new Game(VALORANT, SHADOW_STRIKER, null));
        assertThrows(NullPointerException.class, ()
                -> new Game(VALORANT, SHADOW_STRIKER, Arrays.asList(role, null)));
        assertThrows(IllegalArgumentException.class, Meta.MESSAGE_DUPLICATE_KEY, ()
                -> new Game(VALORANT, SHADOW_STRIKER, List.of(role, role)));
        Meta duplicate = new Meta(new MetaKey(" role "), new MetaValue("Mid"));
        assertThrows(IllegalArgumentException.class, Meta.MESSAGE_DUPLICATE_KEY, ()
                -> new Game(VALORANT, SHADOW_STRIKER, List.of(role, duplicate)));
    }

    @Test
    public void equality_metadata_comparesAllFieldsWithoutChangingIdentity() {
        Meta role = new Meta(new MetaKey("Role"), new MetaValue("Support"));
        Meta style = new Meta(new MetaKey("Style"), new MetaValue("Casual"));
        Game first = new Game(VALORANT, SHADOW_STRIKER, List.of(role, style));
        Game equalGame = new Game(VALORANT, SHADOW_STRIKER, List.of(style, role));
        Game noMetadata = new Game(VALORANT, SHADOW_STRIKER);
        assertEquals(first, equalGame);
        assertEquals(first.hashCode(), equalGame.hashCode());
        assertNotEquals(first, noMetadata);
        assertTrue(first.isSameGame(noMetadata));
        assertNotEquals(first, new Game(VALORANT, SHADOW_STRIKER,
                List.of(new Meta(new MetaKey("Role"), new MetaValue("Mid")), style)));
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Game(null, SHADOW_STRIKER));
        assertThrows(NullPointerException.class, () -> new Game(VALORANT, null));
    }

    @Test
    public void getters_returnFields() {
        Game game = new GameBuilder().withGameName(VALORANT.value).withUsername(SHADOW_STRIKER.value).build();

        assertEquals(VALORANT, game.getGameName());
        assertEquals(SHADOW_STRIKER, game.getUsername());
    }

    @Test
    public void isSameGame() {
        Game game = new GameBuilder().withGameName(VALORANT.value).withUsername(SHADOW_STRIKER.value).build();

        // same object -> returns true
        assertTrue(game.isSameGame(game));

        // null -> returns false
        assertFalse(game.isSameGame(null));

        // same game name, different username -> returns true
        assertTrue(game.isSameGame(new GameBuilder().withGameName("VALORANT").withUsername(PIXEL_MINER.value).build()));

        // different game name -> returns false
        assertFalse(game.isSameGame(new GameBuilder().withGameName(MINECRAFT.value)
                .withUsername(SHADOW_STRIKER.value).build()));
    }

    @Test
    public void equals() {
        Game game = new GameBuilder().withGameName(VALORANT.value).withUsername(SHADOW_STRIKER.value).build();

        assertEquals(game, game);
        assertNotEquals(game, "Valorant");

        // same values -> returns true
        assertEquals(new GameBuilder().withGameName("VALORANT").withUsername(" ShadowStrikerXx ").build(), game);

        // null -> returns false
        assertNotEquals(null, game);

        // different game name -> returns false
        assertNotEquals(new GameBuilder().withGameName(MINECRAFT.value)
                .withUsername(SHADOW_STRIKER.value).build(), game);

        // different username -> returns false
        assertNotEquals(new GameBuilder().withGameName(VALORANT.value).withUsername(PIXEL_MINER.value).build(), game);
    }

    @Test
    public void hashCode_sameValues_returnsSameHashCode() {
        Game firstGame = new GameBuilder().withGameName(VALORANT.value).withUsername(SHADOW_STRIKER.value).build();
        Game secondGame = new GameBuilder().withGameName(" VALORANT ").withUsername(" ShadowStrikerXx ").build();

        assertEquals(firstGame.hashCode(), secondGame.hashCode());
    }

    @Test
    public void toString_returnsFieldsInExpectedFormat() {
        Game game = new GameBuilder().withGameName(VALORANT.value).withUsername(SHADOW_STRIKER.value).build();
        String expected = Game.class.getCanonicalName()
                + "{gameName=Valorant, username=ShadowStrikerXx, metas=[]}";

        assertEquals(expected, game.toString());
    }
}
