package seedu.address.testutil;

import seedu.address.model.GameMates;
import seedu.address.model.friend.Friend;

/**
 * A utility class to help with building GameMates objects.
 * Example usage: <br>
 *     {@code GameMates ab = new GameMatesBuilder().withFriend("John", "Doe").build();}
 */
public class GameMatesBuilder {

    private GameMates gameMates;

    public GameMatesBuilder() {
        gameMates = new GameMates();
    }

    public GameMatesBuilder(GameMates gameMates) {
        this.gameMates = gameMates;
    }

    /**
     * Adds a new {@code Friend} to the {@code GameMates} that we are building.
     */
    public GameMatesBuilder withFriend(Friend friend) {
        gameMates.addFriend(friend);
        return this;
    }

    public GameMates build() {
        return gameMates;
    }
}
