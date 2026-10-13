package seedu.address.testutil;

import seedu.address.model.game.Game;
import seedu.address.model.game.GameName;
import seedu.address.model.game.Username;

/**
 * A utility class to help with building Game objects.
 */
public class GameBuilder {

    public static final String DEFAULT_GAME_NAME = "Minecraft";
    public static final String DEFAULT_USERNAME = "Amy123";

    private Username username;
    private GameName gameName;

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

    public Game build() {
        return new Game(gameName, username);
    }
}
