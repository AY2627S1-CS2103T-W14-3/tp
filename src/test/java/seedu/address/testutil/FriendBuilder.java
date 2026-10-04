package seedu.address.testutil;

import java.util.HashSet;
import java.util.Set;

import seedu.address.model.friend.*;
import seedu.address.model.tag.Tag;
import seedu.address.model.util.SampleDataUtil;

/**
 * A utility class to help with building Friend objects.
 */
public class FriendBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";
    public static final String DEFAULT_REMARK = "";

    private Name name;
    private Phone phone;
    private Email email;
    private Address address;
    private Remark remark;
    private Set<Tag> tags;

    /**
     * Creates a {@code FriendBuilder} with the default details.
     */
    public FriendBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        address = new Address(DEFAULT_ADDRESS);
        remark = new Remark(DEFAULT_REMARK);
        tags = new HashSet<>();
    }

    /**
     * Initializes the FriendBuilder with the data of {@code friendToCopy}.
     */
    public FriendBuilder(Friend friendToCopy) {
        name = friendToCopy.getName();
        phone = friendToCopy.getPhone();
        email = friendToCopy.getEmail();
        address = friendToCopy.getAddress();
        remark = friendToCopy.getRemark();
        tags = new HashSet<>(friendToCopy.getTags());
    }

    /**
     * Sets the {@code Name} of the {@code Friend} that we are building.
     */
    public FriendBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and sets it to the {@code Friend} that we are building.
     */
    public FriendBuilder withTags(String ... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Friend} that we are building.
     */
    public FriendBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Friend} that we are building.
     */
    public FriendBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Friend} that we are building.
     */
    public FriendBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /**
     * Sets the {@code Remark} of the {@code Friend} that we are building.
     */
    public FriendBuilder withRemark(String remark) {
        this.remark = new Remark(remark);
        return this;
    }


    public Friend build() {
        return new Friend(name, phone, email, address, remark, tags);
    }

}
