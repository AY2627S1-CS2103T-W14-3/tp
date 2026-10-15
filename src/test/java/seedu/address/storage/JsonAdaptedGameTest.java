package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.game.Game;
import seedu.address.model.game.meta.Meta;
import seedu.address.testutil.GameBuilder;

/**
 * Tests the stored game format and missing-field validation.
 */
public class JsonAdaptedGameTest {
    @Test
    public void toModelType_metadata_roundTrips() throws Exception {
        Game game = new GameBuilder().addMeta("Role", "Support:Mid").addMeta("Style", "Casual").build();
        String json = JsonUtil.toJsonString(new JsonAdaptedGame(game));
        assertEquals(game, JsonUtil.fromJsonString(json, JsonAdaptedGame.class).toModelType());
    }

    @Test
    public void toModelType_duplicateKeys_throwsIllegalValueException() throws Exception {
        for (String duplicate : List.of("Role", "role", " ROLE ")) {
            String metas = "[{\"key\":\"Role\",\"value\":\"Support\"},"
                    + "{\"key\":\"" + duplicate + "\",\"value\":\"Mid\"}]";
            JsonAdaptedGame game = readGameWithMetas(metas);
            assertThrows(IllegalValueException.class, Meta.MESSAGE_DUPLICATE_KEY, game::toModelType);
        }
        JsonAdaptedGame identicalPairs = readGameWithMetas(
                "[{\"key\":\"Role\",\"value\":\"Support\"},{\"key\":\"Role\",\"value\":\"Support\"}]");
        assertThrows(IllegalValueException.class, Meta.MESSAGE_DUPLICATE_KEY, identicalPairs::toModelType);
    }

    @Test
    public void toModelType_invalidMetadata_throwsIllegalValueException() throws Exception {
        for (String metas : List.of("[null]", "[{}]", "[{\"key\":\"Role\"}]",
                "[{\"value\":\"Support\"}]", "[{\"key\":\"!!!\",\"value\":\"Support\"}]",
                "[{\"key\":\"Role:Type\",\"value\":\"Support\"}]", "[{\"key\":\"Role\",\"value\":\"!!!\"}]")) {
            JsonAdaptedGame game = readGameWithMetas(metas);
            assertThrows(IllegalValueException.class, game::toModelType);
        }
    }

    @Test
    public void toModelType_emptyOrNullMetadata_defaultsToEmpty() throws Exception {
        Game expected = new GameBuilder().withGameName("Valorant").withUsername("player").build();
        assertEquals(expected, readGameWithMetas("[]").toModelType());
        assertEquals(expected, readGameWithMetas("null").toModelType());
    }

    private JsonAdaptedGame readGameWithMetas(String metas) throws Exception {
        return JsonUtil.fromJsonString("{\"name\":\"Valorant\",\"username\":\"player\",\"metas\":" + metas + "}",
                JsonAdaptedGame.class);
    }

    @Test
    public void toModelType_flatJson_roundTrips() throws Exception {
        String json = "{\"name\":\"Valorant\",\"username\":\"player\"}";
        Game expected = new GameBuilder().withGameName("Valorant").withUsername("player").build();
        JsonAdaptedGame adapted = JsonUtil.fromJsonString(json, JsonAdaptedGame.class);
        assertEquals(expected, adapted.toModelType());
        assertEquals(expected, JsonUtil.fromJsonString(JsonUtil.toJsonString(new JsonAdaptedGame(expected)),
                JsonAdaptedGame.class).toModelType());
    }

    @Test
    public void toModelType_missingFields_throwsIllegalValueException() throws Exception {
        JsonAdaptedGame missingName = JsonUtil.fromJsonString("{\"username\":\"player\"}", JsonAdaptedGame.class);
        assertThrows(IllegalValueException.class, "Game's GameName field is missing!", missingName::toModelType);
        JsonAdaptedGame missingUsername = JsonUtil.fromJsonString("{\"name\":\"Valorant\"}", JsonAdaptedGame.class);
        assertThrows(IllegalValueException.class, "Game's Username field is missing!", missingUsername::toModelType);
    }
}
