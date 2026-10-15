package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.game.GameName;
import seedu.address.testutil.TypicalFriends;

public class CommandResultTest {
    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns true
        assertEquals(new CommandResult("feedback"), commandResult);
        assertEquals(new CommandResult("feedback", false, false), commandResult);

        // null -> returns false
        assertNotEquals(null, commandResult);

        // different types -> returns false
        assertNotEquals(0.5f, commandResult);

        // different feedbackToUser value -> returns false
        assertNotEquals(new CommandResult("different"), commandResult);

        // different showHelp value -> returns false
        assertNotEquals(new CommandResult("feedback", true, false), commandResult);

        // different exit value -> returns false
        assertNotEquals(new CommandResult("feedback", false, true), commandResult);

        // different friendToShow value -> returns false
        assertNotEquals(new CommandResult("feedback", TypicalFriends.ALICE), commandResult);
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());

        // different feedbackToUser value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());

        // different showHelp value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", true, false).hashCode());

        // different exit value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", false, true).hashCode());

        // different friendToShow value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", TypicalFriends.ALICE).hashCode());
    }

    @Test
    public void gameFilter_payloadAndValueSemantics() {
        GameName gameFilter = new GameName("Valorant");
        CommandResult result = new CommandResult("feedback", TypicalFriends.ALICE, gameFilter);
        CommandResult copy = new CommandResult("feedback", TypicalFriends.ALICE, new GameName("valorant"));
        assertEquals(Optional.of(TypicalFriends.ALICE), result.getFriendToShow());
        assertEquals(Optional.of(gameFilter), result.getGameFilter());
        assertEquals(result, copy);
        assertEquals(result.hashCode(), copy.hashCode());
        assertNotEquals(result, new CommandResult("feedback", TypicalFriends.ALICE));
        assertNotEquals(result, new CommandResult("feedback", TypicalFriends.ALICE, new GameName("Minecraft")));
        assertNotEquals(result, new CommandResult("feedback", TypicalFriends.BENSON, gameFilter));
        assertTrue(new CommandResult("feedback").getGameFilter().isEmpty());
        assertTrue(new CommandResult("feedback", TypicalFriends.ALICE).getGameFilter().isEmpty());
        assertTrue(new CommandResult("feedback", false, false).getGameFilter().isEmpty());
        assertThrows(NullPointerException.class, () -> new CommandResult("feedback", null, gameFilter));
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName() + "{feedbackToUser="
                + commandResult.getFeedbackToUser() + ", showHelp=" + commandResult.isShowHelp()
                + ", exit=" + commandResult.isExit() + ", friendToShow=" + commandResult.getFriendToShow()
                + ", gameFilter=" + commandResult.getGameFilter() + "}";
        assertEquals(expected, commandResult.toString());
    }

    @Test
    public void toStringMethod_withGameFilter() {
        CommandResult result = new CommandResult("feedback", TypicalFriends.ALICE, new GameName("Valorant"));
        assertEquals(CommandResult.class.getCanonicalName() + "{feedbackToUser=feedback, showHelp=false, exit=false"
                + ", friendToShow=" + result.getFriendToShow() + ", gameFilter=Optional[Valorant]}", result.toString());
    }
}
