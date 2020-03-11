package com.mark.zumo.client.customer.entity;

/**
 * Created by mark on 19. 8. 4.
 */
public class Token {
    public String user_id;
    public String token_value;

    public Token(final String user_id, final String token_value) {
        this.user_id = user_id;
        this.token_value = token_value;
    }
}