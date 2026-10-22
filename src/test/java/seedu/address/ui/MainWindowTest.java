package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.GameMatesParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.GameMates;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.friend.Friend;
import seedu.address.testutil.FriendBuilder;
import seedu.address.testutil.GameBuilder;

class MainWindowTest {

    @Test
    void selectedFriendAndCommandResult_updateDetailsPanel() {
        FxTestUtil.runAndWait(() -> {
            Friend friend = new FriendBuilder().withName("Alice").build();
            StubLogic logic = new StubLogic(friend);
            Stage stage = new Stage();
            MainWindow mainWindow = new MainWindow(stage, logic, Path.of("test-data.json"));
            mainWindow.fillInnerParts();
            mainWindow.getRoot().getScene().getRoot().applyCss();

            getFriendList(mainWindow).getSelectionModel().select(friend);
            assertEquals("Friend details", getLabel(mainWindow, "#title").getText());
            assertEquals("Alice", getLabel(mainWindow, "#name").getText());

            logic.setNextResult(new CommandResult("Listed friends"));
            executeCommand(mainWindow);
            assertEquals("Your details", getLabel(mainWindow, "#title").getText());
        });
    }

    @Test
    void gameList_commandsAndSelection_filterAndClearGameCards() {
        FxTestUtil.runAndWait(() -> {
            Friend alice = new FriendBuilder().withName("Alice").withGames(
                    new GameBuilder().withGameName("Valorant").build(), new GameBuilder().build()).build();
            Friend bob = new FriendBuilder().withName("Bob").withGames(
                    new GameBuilder().withGameName("Stardew Valley").build()).build();
            StubLogic logic = new StubLogic(alice, bob);
            MainWindow window = new MainWindow(new Stage(), logic, Path.of("test-data.json"));
            window.fillInnerParts();
            window.getRoot().getScene().getRoot().applyCss();

            executeCommand(window, "gamelist 1 g/valorant");
            assertEquals("Alice", getLabel(window, "#name").getText());
            assertEquals(1, getGames(window).getChildren().size());
            assertEquals("Valorant", getLabel(window, ".details-game-name").getText());
            assertEquals(2, getFriendList(window).getItems().size());

            executeCommand(window, "gamelist 1");
            assertEquals(2, getGames(window).getChildren().size());
            executeCommand(window, "gamelist 1 g/Valorant");
            getFriendList(window).getSelectionModel().select(bob);
            assertEquals("Bob", getLabel(window, "#name").getText());
            assertEquals("Stardew Valley", getLabel(window, ".details-game-name").getText());

            executeCommand(window, "gamelist 1 g/Unknown");
            assertEquals("No matching game: Unknown", ((Label) getGames(window).getChildren().getFirst()).getText());
            executeCommand(window, "find Alice");
            assertEquals(2, getGames(window).getChildren().size());
            assertEquals(1, getFriendList(window).getItems().size());

            executeCommand(window, "gamelist 1 g/minecraft");
            assertEquals("Minecraft", getLabel(window, ".details-game-name").getText());
            assertEquals(1, getFriendList(window).getItems().size());
            assertEquals(2, alice.getGames().size());
        });
    }

    @Test
    void gameList_invalidCommands_preserveDetailsAndGameFilter() {
        FxTestUtil.runAndWait(() -> {
            Friend friend = new FriendBuilder().withName("Alice").withGames(
                    new GameBuilder().withGameName("Valorant").build(), new GameBuilder().build()).build();
            MainWindow window = new MainWindow(new Stage(), new StubLogic(friend), Path.of("test-data.json"));
            window.fillInnerParts();
            window.getRoot().getScene().getRoot().applyCss();
            executeCommand(window, "gamelist 1 g/Valorant");

            for (String input : new String[]{"gamelist 0", "gamelist 1 g/", "gamelist 2"}) {
                InvocationTargetException failure = assertThrows(InvocationTargetException.class, () ->
                        invokeCommand(window, input));
                Class<? extends Exception> expectedType = input.equals("gamelist 2")
                        ? CommandException.class : ParseException.class;
                assertInstanceOf(expectedType, failure.getCause());
                assertEquals("Alice", getLabel(window, "#name").getText());
                assertEquals(1, getGames(window).getChildren().size());
                assertEquals("Valorant", getLabel(window, ".details-game-name").getText());
            }
        });
    }

    @Test
    void gameEdit_updatesFriendAndMyselfDetailsAndPreservesFailureView() {
        FxTestUtil.runAndWait(() -> {
            Friend friend = new FriendBuilder().withName("Alice").withGames(
                    new GameBuilder().withGameName("Valorant").build(), new GameBuilder().build()).build();
            StubLogic logic = new StubLogic(friend);
            MainWindow window = new MainWindow(new Stage(), logic, Path.of("test-data.json"));
            window.fillInnerParts();
            window.getRoot().getScene().getRoot().applyCss();
            executeCommand(window, "gamelist 1 g/Valorant");
            executeCommand(window, "gameedit 1 g/Valorant n/New Game u/newPlayer m/Role:Tank");
            assertEquals("Alice", getLabel(window, "#name").getText());
            assertEquals(2, getGames(window).getChildren().size());
            assertEquals("Friend details", getLabel(window, "#title").getText());
            assertEquals("New Game", getGames(window).getChildren().get(1)
                    .lookup(".details-game-name") instanceof Label label ? label.getText() : "");
            assertThrows(InvocationTargetException.class, () ->
                    invokeCommand(window, "gameedit 1 g/New Game n/Minecraft"));
            assertEquals("Alice", getLabel(window, "#name").getText());
            logic.model.setMyself(new FriendBuilder(friend).withName("Own Profile").build());
            executeCommand(window, "gameedit myself g/Valorant n/My Game");
            assertEquals("Your details", getLabel(window, "#title").getText());
            assertEquals("Own Profile", getLabel(window, "#name").getText());
        });
    }

    private void executeCommand(MainWindow mainWindow) {
        executeCommand(mainWindow, "list");
    }

    private void executeCommand(MainWindow mainWindow, String commandText) {
        try {
            invokeCommand(mainWindow, commandText);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Could not execute command through the main window", exception);
        }
    }

    private void invokeCommand(MainWindow mainWindow, String commandText) throws ReflectiveOperationException {
        Method executeCommand = MainWindow.class.getDeclaredMethod("executeCommand", String.class);
        executeCommand.setAccessible(true);
        executeCommand.invoke(mainWindow, commandText);
    }

    private VBox getGames(MainWindow mainWindow) {
        return (VBox) mainWindow.getRoot().getScene().getRoot().lookup("#games");
    }

    @SuppressWarnings("unchecked")
    private ListView<Friend> getFriendList(MainWindow mainWindow) {
        return (ListView<Friend>) mainWindow.getRoot().getScene().getRoot().lookup("#friendListView");
    }

    private Label getLabel(MainWindow mainWindow, String selector) {
        return (Label) mainWindow.getRoot().getScene().getRoot().lookup(selector);
    }

    private static class StubLogic implements Logic {
        private final Model model;
        private CommandResult nextResult;

        StubLogic(Friend... friends) {
            GameMates gameMates = new GameMates();
            for (Friend friend : friends) {
                gameMates.addFriend(friend);
            }
            model = new ModelManager(gameMates, new UserPrefs());
        }

        void setNextResult(CommandResult nextResult) {
            this.nextResult = nextResult;
        }

        @Override
        public CommandResult execute(String commandText) throws CommandException, ParseException {
            return nextResult == null ? new GameMatesParser().parseCommand(commandText).execute(model) : nextResult;
        }

        @Override
        public Friend getMyself() {
            return model.getGameMates().getMyself();
        }

        @Override
        public ObservableList<Friend> getFilteredFriendList() {
            return model.getFilteredFriendList();
        }

        @Override
        public GuiSettings getGuiSettings() {
            return new GuiSettings();
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            // This test does not close the window.
        }
    }
}
