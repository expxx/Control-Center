package dev.expx.ctrlctr.center.communication.rabbit.data;

import lombok.Getter;

/**
 * Represents a set of authentication data.
 */
@Getter
public class AuthSet {

    private final String user;
    private final String pass;

    /**
     * Creates a new set of authentication data.
     * @param user The username.
     * @param pass The password.
     */
    public AuthSet(
            final String user,
            final String pass
    ) {
        this.user = user;
        this.pass = pass;
    }
}
