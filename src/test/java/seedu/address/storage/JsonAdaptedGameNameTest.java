package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.game.GameName;

/**
 * Tests storage validation and serialization of GameName.
 */
public class JsonAdaptedGameNameTest {
    @Test
    public void toModelType_validValue_roundTripsAsJsonString() throws Exception {
        GameName value = new GameName("Player One");
        String json = JsonUtil.toJsonString(new JsonAdaptedGameName(value));
        assertEquals("\"Player One\"", json);
        assertEquals(value, JsonUtil.fromJsonString(json, JsonAdaptedGameName.class).toModelType());
    }

    @Test
    public void toModelType_invalidOrMissingValue_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, GameName.MESSAGE_CONSTRAINTS,
                new JsonAdaptedGameName(" ")::toModelType);
        String message = String.format(JsonAdaptedGame.MISSING_FIELD_MESSAGE_FORMAT, "GameName");
        assertThrows(IllegalValueException.class, message,
                new JsonAdaptedGameName((String) null)::toModelType);
    }
}
