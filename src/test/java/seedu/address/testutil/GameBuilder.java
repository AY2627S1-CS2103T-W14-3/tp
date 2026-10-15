package seedu.address.testutil;

import java.util.ArrayList;
import java.util.List;

import seedu.address.model.game.Game;
import seedu.address.model.game.GameName;
import seedu.address.model.game.Username;
import seedu.address.model.game.meta.Meta;
import seedu.address.model.game.meta.MetaKey;
import seedu.address.model.game.meta.MetaValue;

/**
 * A utility class to help with building Game objects.
 */
public class GameBuilder {

    public static final String DEFAULT_GAME_NAME = "Minecraft";
    public static final String DEFAULT_USERNAME = "Amy123";

    private Username username;
    private GameName gameName;
    private List<Meta> metas = new ArrayList<>();

    /**
     * Creates a {@code GameBuilder} with the default details.
     */
    public GameBuilder() {
        gameName = new GameName(DEFAULT_GAME_NAME);
        username = new Username(DEFAULT_USERNAME);
    }

    /**
     * Initializes the GameBuilder with the data of {@code gameToCopy}.
     */
    public GameBuilder(Game gameToCopy) {
        gameName = gameToCopy.getGameName();
        username = gameToCopy.getUsername();
        metas = new ArrayList<>(gameToCopy.getMetas());
    }

    /**
     * Sets the {@code GameName} of the {@code Game} that we are building.
     */
    public GameBuilder withGameName(String name) {
        this.gameName = new GameName(name);
        return this;
    }

    /**
     * Sets the {@code Username} of the {@code Game} that we are building.
     */
    public GameBuilder withUsername(String username) {
        this.username = new Username(username);
        return this;
    }

    /**
     * Sets the metadata of the game being built.
     */
    public GameBuilder withMetas(Meta... metas) {
        this.metas = new ArrayList<>(List.of(metas));
        return this;
    }

    /**
     * Appends a metadata pair using the model's key and value validation.
     * Duplicate keys are rejected when the game is built.
     */
    public GameBuilder addMeta(String key, String value) {
        metas.add(new Meta(new MetaKey(key), new MetaValue(value)));
        return this;
    }

    public Game build() {
        return new Game(gameName, username, metas);
    }
}
