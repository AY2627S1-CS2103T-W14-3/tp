package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.game.Game;
import seedu.address.model.game.meta.Meta;
import seedu.address.model.game.meta.MetaKey;

/**
 * Jackson-friendly version of {@link Game}.
 */
class JsonAdaptedGame {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Game's %s field is missing!";

    private final JsonAdaptedGameName name;
    private final JsonAdaptedUsername username;
    private final List<JsonAdaptedMeta> metas = new ArrayList<>();

    /**
     * Constructs an adapted game without metadata.
     */
    public JsonAdaptedGame(String name, String username) {
        this(name, username, List.of());
    }

    /**
     * Constructs a {@code JsonAdaptedGame} with the given game details.
     */
    @JsonCreator
    public JsonAdaptedGame(@JsonProperty("name") String name, @JsonProperty("username") String username,
            @JsonProperty("metas") List<JsonAdaptedMeta> metas) {
        this.name = new JsonAdaptedGameName(name);
        this.username = new JsonAdaptedUsername(username);
        if (metas != null) {
            this.metas.addAll(metas);
        }
    }

    /**
     * Converts a given {@code Game} into this class for Jackson use.
     */
    public JsonAdaptedGame(Game game) {
        this.name = new JsonAdaptedGameName(game.getGameName());
        this.username = new JsonAdaptedUsername(game.getUsername());
        metas.addAll(game.getMetas().stream().map(JsonAdaptedMeta::new).toList());
    }

    /**
     * Converts this Jackson-friendly adapted game object into the model's {@code Game} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted game.
     */
    public Game toModelType() throws IllegalValueException {
        List<Meta> modelMetas = new ArrayList<>();
        Set<MetaKey> keys = new HashSet<>();
        for (JsonAdaptedMeta meta : metas) {
            if (meta == null) {
                throw new IllegalValueException(Meta.MESSAGE_CONSTRAINTS);
            }
            Meta modelMeta = meta.toModelType();
            if (!keys.add(modelMeta.getKey())) {
                throw new IllegalValueException(Meta.MESSAGE_DUPLICATE_KEY);
            }
            modelMetas.add(modelMeta);
        }
        return new Game(name.toModelType(), username.toModelType(), modelMetas);
    }

}
