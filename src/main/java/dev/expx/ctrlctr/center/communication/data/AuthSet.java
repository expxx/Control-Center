package dev.expx.ctrlctr.center.communication.data;

import lombok.Getter;

/**
 * Represents a set of authentication data.
 */
public record AuthSet(String user, String pass) {

    /**
     * Creates a new set of authentication data.
     *
     * @param user The username.
     * @param pass The password.
     */
    public AuthSet {
    }
}
