package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.game.Game;
import seedu.address.testutil.GameBuilder;

/**
 * Tests the stored game format and missing-field validation.
 */
public class JsonAdaptedGameTest {
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
