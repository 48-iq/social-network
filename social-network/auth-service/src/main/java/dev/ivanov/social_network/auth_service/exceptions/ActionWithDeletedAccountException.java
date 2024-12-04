package dev.ivanov.social_network.auth_service.exceptions;

import lombok.experimental.StandardException;

@StandardException
public class ActionWithDeletedAccountException extends RuntimeException {
}
