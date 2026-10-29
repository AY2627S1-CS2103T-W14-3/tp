package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.GameMatesParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.FriendListScope;
import seedu.address.model.GameMates;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.friend.Friend;
import seedu.address.testutil.FriendBuilder;
import seedu.address.testutil.GameBuilder;

class MainWindowTest {

    @Test
    void gameAdd_metadataAppearsForFriendAndProfile() {
        FxTestUtil.runAndWait(() -> {
            MainWindow window = new MainWindow(new Stage(),
                    new StubLogic(new FriendBuilder().withName("Alice").build()), Path.of("test-data.json"));
            window.fillInnerParts();
            window.getRoot().getScene().getRoot().applyCss();

            executeCommand(window, "gameadd 1 g/Valorant u/alice m/Role:Support:Mid");
            executeCommand(window, "gamelist 1 g/Valorant");
            assertEquals("Friend details", getLabel(window, "#title").getText());
            assertEquals("Role: Support:Mid", getLabel(window, ".details-game-metadata").getText());

            executeCommand(window, "edit myself n/Alex");
            executeCommand(window, "gameadd myself g/Minecraft u/player m/Mode:Creative");
            assertEquals("Your details", getLabel(window, "#title").getText());
            assertEquals("Mode: Creative", getLabel(window, ".details-game-metadata").getText());
        });
    }

    @Test
    void gameAdd_invalidMetadataPreservesDisplayedDetailsAndGameFilter() {
        FxTestUtil.runAndWait(() -> {
            Friend friend = new FriendBuilder().withName("Alice").withGames(
                    new GameBuilder().withGameName("Valorant").addMeta("Role", "Support").build(),
                    new GameBuilder().build()).build();
            MainWindow window = new MainWindow(new Stage(), new StubLogic(friend), Path.of("test-data.json"));
            window.fillInnerParts();
            window.getRoot().getScene().getRoot().applyCss();
            executeCommand(window, "gamelist 1 g/Valorant");

            for (String input : new String[]{"m/Role", "m/Role:", "m/:Support", "m/Role:Support m/",
                "m/Role:Support m/ROLE:Healer"}) {
                for (String selector : new String[]{"1", "myself"}) {
                    InvocationTargetException failure = assertThrows(InvocationTargetException.class, () ->
                            invokeCommand(window, "gameadd " + selector + " g/Apex u/player " + input));
                    assertInstanceOf(ParseException.class, failure.getCause());
                    assertEquals("Alice", getLabel(window, "#name").getText());
                    assertEquals(1, getGames(window).getChildren().size());
                    assertEquals("Valorant", getLabel(window, ".details-game-name").getText());
                    assertEquals("Role: Support", getLabel(window, ".details-game-metadata").getText());
                }
            }
        });
    }

    @Test
    void manyMetadataRows_canScrollToLastRow() {
        FxTestUtil.runAndWait(() -> {
            GameBuilder game = new GameBuilder();
            for (int i = 0; i < 30; i++) {
                game.addMeta(String.format("Field%02d", i), "Value " + i);
            }
            Friend friend = new FriendBuilder().withGames(game.build()).build();
            MainWindow window = new MainWindow(new Stage(), new StubLogic(friend), Path.of("test-data.json"));
            window.fillInnerParts();
            var root = window.getRoot().getScene().getRoot();
            root.applyCss();
            executeCommand(window, "gamelist 1");
            root.resize(900, 600);
            root.applyCss();
            root.layout();
            ScrollPane scroll = (ScrollPane) root.lookup(".friend-details-scroll-pane");
            assertTrue(scroll.getContent().getBoundsInLocal().getHeight() > scroll.getViewportBounds().getHeight());

            scroll.setVvalue(scroll.getVmax());
            root.layout();

            VBox metadata = (VBox) scroll.getContent().lookup("#metadata");
            Label last = (Label) metadata.getChildren().getLast();
            var lastBounds = last.localToScene(last.getBoundsInLocal());
            var viewport = scroll.lookup(".viewport");
            var viewportBounds = viewport.localToScene(viewport.getBoundsInLocal());
            assertEquals("Field29: Value 29", last.getText());
            assertTrue(lastBounds.getMinY() >= viewportBounds.getMinY());
            assertTrue(lastBounds.getMaxY() <= viewportBounds.getMaxY() + 1);
        });
    }

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
            assertNull(getFriendList(mainWindow).getSelectionModel().getSelectedItem());
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
            assertEquals(alice, getFriendList(window).getSelectionModel().getSelectedItem());
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
            assertEquals(alice, getFriendList(window).getSelectionModel().getSelectedItem());
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
    void gameList_myself_selectsProfileAndClearsFilter() {
        FxTestUtil.runAndWait(() -> {
            Friend profile = new FriendBuilder().withName("Alice").withGames(
                    new GameBuilder().withGameName("Valorant").build(), new GameBuilder().build()).build();
            // An identical friend must still be labelled as a friend when selected by numeric index.
            StubLogic logic = new StubLogic(profile);
            logic.model.setMyself(profile);
            MainWindow window = new MainWindow(new Stage(), logic, Path.of("test-data.json"));
            window.fillInnerParts();
            window.getRoot().getScene().getRoot().applyCss();

            executeCommand(window, "gamelist 1");
            assertEquals("Friend details", getLabel(window, "#title").getText());
            executeCommand(window, "gamelist myself g/valorant");
            assertEquals("Your details", getLabel(window, "#title").getText());
            assertEquals(1, getGames(window).getChildren().size());
            assertEquals("Valorant", getLabel(window, ".details-game-name").getText());

            executeCommand(window, "gamelist myself");
            assertEquals(2, getGames(window).getChildren().size());
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

    @Test
    void gameEdit_archivedFriend_keepsArchiveHeadingAndUpdatedDetails() {
        FxTestUtil.runAndWait(() -> {
            Friend archived = new FriendBuilder().withName("Alice").withGames(
                    new GameBuilder().withGameName("Valorant").build()).build().withArchived(true);
            MainWindow window = new MainWindow(new Stage(), new StubLogic(archived), Path.of("test-data.json"));
            window.fillInnerParts();
            window.getRoot().getScene().getRoot().applyCss();
            executeCommand(window, "archivelist");
            executeCommand(window, "gamelist 1 g/Valorant");
            executeCommand(window, "gameedit 1 g/Valorant n/New Game");
            assertEquals("Archived friends", getLabel(window, "#listHeading").getText());
            assertEquals(1, getFriendList(window).getItems().size());
            assertTrue(getFriendList(window).getItems().getFirst().isArchived());
            assertEquals("Alice", getLabel(window, "#name").getText());
            assertEquals("Friend details", getLabel(window, "#title").getText());
            assertEquals("New Game", getLabel(window, ".details-game-name").getText());
        });
    }

    @Test
    void archiveCommands_updateHeadingAndClearRemovedFriendsDetails() {
        FxTestUtil.runAndWait(() -> {
            Friend alice = new FriendBuilder().withName("Alice").build();
            MainWindow window = new MainWindow(new Stage(), new StubLogic(alice), Path.of("test-data.json"));
            window.fillInnerParts();
            window.getRoot().getScene().getRoot().applyCss();
            getFriendList(window).getSelectionModel().selectFirst();
            executeCommand(window, "archive 1");
            assertEquals("Your details", getLabel(window, "#title").getText());
            assertEquals("Active friends", getLabel(window, "#listHeading").getText());
            executeCommand(window, "archivelist");
            assertEquals("Archived friends", getLabel(window, "#listHeading").getText());
            assertEquals(1, getFriendList(window).getItems().size());
            getFriendList(window).getSelectionModel().selectFirst();
            executeCommand(window, "unarchive 1");
            assertEquals("Your details", getLabel(window, "#title").getText());
            assertEquals("Archived friends", getLabel(window, "#listHeading").getText());
            assertEquals(0, getFriendList(window).getItems().size());
            executeCommand(window, "list");
            assertEquals("Active friends", getLabel(window, "#listHeading").getText());
            assertEquals(1, getFriendList(window).getItems().size());
        });
    }

    @Test
    void globalSearch_preservesResultsWhenArchivingAndRestoring() {
        FxTestUtil.runAndWait(() -> {
            Friend alice = new FriendBuilder().withName("Alice").build();
            Friend bob = new FriendBuilder().withName("Bob").build().withArchived(true);
            MainWindow window = new MainWindow(new Stage(), new StubLogic(alice, bob), Path.of("test-data.json"));
            window.fillInnerParts();
            window.getRoot().getScene().getRoot().applyCss();
            executeCommand(window, "find Alice Bob");
            assertEquals("Search results", getLabel(window, "#listHeading").getText());
            assertEquals(2, getFriendList(window).getItems().size());
            executeCommand(window, "archive 1");
            assertEquals(alice.withArchived(true), getFriendList(window).getItems().getFirst());
            assertEquals(2, getFriendList(window).getItems().size());
            assertEquals("Search results", getLabel(window, "#listHeading").getText());
            executeCommand(window, "unarchive 2");
            assertEquals(bob.withArchived(false), getFriendList(window).getItems().get(1));
            assertEquals("Search results", getLabel(window, "#listHeading").getText());
            executeCommand(window, "find Nobody");
            assertEquals(0, getFriendList(window).getItems().size());
            assertEquals("Search results", getLabel(window, "#listHeading").getText());
            executeCommand(window, "archivelist");
            assertEquals("Archived friends", getLabel(window, "#listHeading").getText());
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
        public FriendListScope getFriendListScope() {
            return model.getFriendListScope();
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
