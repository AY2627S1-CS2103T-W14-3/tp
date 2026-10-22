package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.game.Username;

/**
 * Tests storage validation and serialization of Username.
 */
public class JsonAdaptedUsernameTest {
    @Test
    public void toModelType_validValue_roundTripsAsJsonString() throws Exception {
        Username value = new Username("Player One");
        String json = JsonUtil.toJsonString(new JsonAdaptedUsername(value));
        assertEquals("\"Player One\"", json);
        assertEquals(value, JsonUtil.fromJsonString(json, JsonAdaptedUsername.class).toModelType());
    }

    @Test
    public void toModelType_invalidValue_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, Username.MESSAGE_CONSTRAINTS,
                new JsonAdaptedUsername(" ")::toModelType);
    }

    @Test
    public void toModelType_emptyOrMissingValue_returnsEmptyUsername() throws Exception {
        assertEquals(Username.EMPTY, new JsonAdaptedUsername((String) null).toModelType());
        assertEquals(Username.EMPTY, new JsonAdaptedUsername("").toModelType());
        String json = JsonUtil.toJsonString(new JsonAdaptedUsername(Username.EMPTY));
        assertEquals("\"\"", json);
        assertEquals(Username.EMPTY, JsonUtil.fromJsonString(json, JsonAdaptedUsername.class).toModelType());
    }
}
