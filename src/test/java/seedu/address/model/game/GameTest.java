package seedu.address.model.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.GameBuilder;

public class GameTest {

    private static final GameName VALORANT = new GameName("Valorant");
    private static final GameName MINECRAFT = new GameName("Minecraft");
    private static final Username SHADOW_STRIKER = new Username("ShadowStrikerXx");
    private static final Username PIXEL_MINER = new Username("PixelMiner");

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
                + "{gameName=Valorant, username=ShadowStrikerXx}";

        assertEquals(expected, game.toString());
    }
}
