package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.game.Game;

/**
 * Jackson-friendly version of {@link Game}.
 */
class JsonAdaptedGame {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Game's %s field is missing!";

    private final JsonAdaptedGameName name;
    private final JsonAdaptedUsername username;

    /**
     * Constructs a {@code JsonAdaptedGame} with the given game details.
     */
    @JsonCreator
    public JsonAdaptedGame(@JsonProperty("name") String name, @JsonProperty("username") String username) {
        this.name = new JsonAdaptedGameName(name);
        this.username = new JsonAdaptedUsername(username);
    }

    /**
     * Converts a given {@code Game} into this class for Jackson use.
     */
    public JsonAdaptedGame(Game game) {
        this.name = new JsonAdaptedGameName(game.getGameName());
        this.username = new JsonAdaptedUsername(game.getUsername());
    }

    /**
     * Converts this Jackson-friendly adapted game object into the model's {@code Game} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted game.
     */
    public Game toModelType() throws IllegalValueException {
        return new Game(name.toModelType(), username.toModelType());
    }

}
