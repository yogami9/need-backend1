package com.needbackend_app.needapp.auth.websocketauth;

import java.security.Principal;


import java.security.Principal;

public record StompPrincipal(String name) implements Principal {
    @Override
    public String getName() {
        return name;
    }
}


