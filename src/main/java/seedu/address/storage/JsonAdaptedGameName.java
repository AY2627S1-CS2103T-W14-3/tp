package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.game.GameName;

/**
 * Jackson-friendly version of {@link GameName}, stored as a JSON string.
 */
class JsonAdaptedGameName {
    private final String value;

    /**
     * Constructs an adapter from its stored value.
     */
    @JsonCreator
    public JsonAdaptedGameName(String value) {
        this.value = value;
    }

    /**
     * Converts a model value for Jackson use.
     */
    public JsonAdaptedGameName(GameName source) {
        value = source.value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Converts the stored value to the model, validating its constraints.
     *
     * @throws IllegalValueException if the stored value is missing or invalid.
     */
    public GameName toModelType() throws IllegalValueException {
        if (value == null) {
            throw new IllegalValueException(String.format(JsonAdaptedGame.MISSING_FIELD_MESSAGE_FORMAT,
                    GameName.class.getSimpleName()));
        }
        if (!GameName.isValidGameName(value)) {
            throw new IllegalValueException(GameName.MESSAGE_CONSTRAINTS);
        }
        return new GameName(value);
    }
}
