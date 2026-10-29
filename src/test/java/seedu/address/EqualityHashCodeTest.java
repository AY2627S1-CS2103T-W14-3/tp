package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_FRIENDS;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.commands.ArchiveListCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditFriendDescriptor;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.GameAddCommand;
import seedu.address.logic.commands.GameDeleteCommand;
import seedu.address.logic.commands.GameEditCommand;
import seedu.address.logic.commands.GameEditCommand.EditGameDescriptor;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.PinCommand;
import seedu.address.logic.commands.SquadCommand;
import seedu.address.logic.commands.UnarchiveCommand;
import seedu.address.logic.commands.UnpinCommand;
import seedu.address.model.ModelManager;
import seedu.address.model.friend.FriendSortCriterion;
import seedu.address.model.friend.GameNameMatchesPredicate;
import seedu.address.model.friend.Name;
import seedu.address.model.friend.NameContainsKeywordsPredicate;
import seedu.address.model.game.Game;
import seedu.address.model.game.GameName;
import seedu.address.model.game.Username;
import seedu.address.testutil.EditFriendDescriptorBuilder;
import seedu.address.testutil.FriendBuilder;

/** Regression tests for value equality when objects are used in hash-based collections. */
public class EqualityHashCodeTest {

    @TestFactory
    public Stream<DynamicTest> equalObjects_workAsHashKeys() {
        Stream.Builder<Supplier<?>> factories = Stream.builder();
        factories.add(() -> Index.fromOneBased(1));
        factories.add(Index::myself);
        factories.add(() -> new NameContainsKeywordsPredicate(List.of("Alice", "Bob")));
        factories.add(() -> new NameContainsKeywordsPredicate(List.of()));
        factories.add(() -> new GameNameMatchesPredicate(new GameName("Valorant")));
        factories.add(ModelManager::new);
        factories.add(() -> {
            ModelManager model = new ModelManager();
            model.addFriend(new FriendBuilder().build());
            model.updateArchivedFriendList(PREDICATE_SHOW_ALL_FRIENDS);
            return model;
        });
        factories.add(EditFriendDescriptor::new);
        factories.add(() -> new EditFriendDescriptorBuilder().withName("Alice").withTags("friend").build());
        factories.add(() -> new AddCommand(new FriendBuilder().build()));
        factories.add(() -> new ArchiveCommand(Index.fromOneBased(1)));
        factories.add(ArchiveListCommand::new);
        factories.add(() -> new ArchiveListCommand(new GameNameMatchesPredicate(new GameName("Valorant"))));
        factories.add(() -> new DeleteCommand(Index.fromOneBased(1)));
        factories.add(() -> new EditCommand(Index.myself(),
                new EditFriendDescriptorBuilder().withName("Alice").build()));
        factories.add(() -> new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice"))));
        factories.add(() -> new GameAddCommand(Index.myself(),
                new Game(new GameName("Valorant"), new Username("alice"))));
        factories.add(() -> new GameDeleteCommand(Index.myself(), new GameName("Valorant")));
        factories.add(() -> new GameEditCommand(Index.myself(), new GameName("Valorant"),
                new EditGameDescriptor(null, new Username("alice"), Set.of())));
        factories.add(ListCommand::new);
        factories.add(() -> new ListCommand(new GameNameMatchesPredicate(new GameName("Valorant")),
                FriendSortCriterion.NAME, true));
        factories.add(() -> new PinCommand(Index.fromOneBased(1)));
        factories.add(() -> new SquadCommand(new GameName("Valorant"), 2));
        factories.add(() -> new UnarchiveCommand(Index.fromOneBased(1)));
        factories.add(() -> new UnpinCommand(Index.fromOneBased(1)));
        return factories.build()
                .map(factory -> {
                    Object first = factory.get();
                    Object second = factory.get();
                    return DynamicTest.dynamicTest(first.getClass().getSimpleName() + ": " + first, () ->
                            assertEqualHashKeys(first, second));
                });
    }

    @TestFactory
    public Stream<DynamicTest> normalizedValues_workAsHashKeys() {
        return Stream.of(
                new Object[] {Index.fromZeroBased(0), Index.fromOneBased(1)},
                new Object[] {new GameNameMatchesPredicate(new GameName("Valorant")),
                    new GameNameMatchesPredicate(new GameName("VALORANT"))},
                new Object[] {new Name("Alice Smith"), new Name("ALICE  SMITH")},
                new Object[] {new AddCommand(new FriendBuilder().withName("Alice Smith").build()),
                    new AddCommand(new FriendBuilder().withName("ALICE  SMITH").build())},
                new Object[] {new EditFriendDescriptorBuilder().withName("Alice Smith").build(),
                    new EditFriendDescriptorBuilder().withName("ALICE  SMITH").build()})
                .map(pair -> DynamicTest.dynamicTest(pair[0].getClass().getSimpleName(), () ->
                        assertEqualHashKeys(pair[0], pair[1])));
    }

    private static void assertEqualHashKeys(Object first, Object second) {
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        Set<Object> set = new HashSet<>();
        set.add(first);
        assertTrue(set.contains(second));
        set.add(second);
        assertEquals(1, set.size());
        HashMap<Object, String> map = new HashMap<>();
        map.put(first, "value");
        assertEquals("value", map.get(second));
    }
}
