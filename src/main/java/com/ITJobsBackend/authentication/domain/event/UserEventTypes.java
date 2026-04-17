package com.ITJobsBackend.authentication.domain.event;

public final class UserEventTypes {

  public static final String USER_REGISTERED = "user.registered";
  public static final String USER_ACTIVATED = "user.activated";
  public static final String USER_DEACTIVATED = "user.deactivated";
  public static final String EMAIL_VERIFIED = "email.verified";
  public static final String EMAIL_CHANGED = "email.changed";
  public static final String PASSWORD_CHANGED = "user.password_changed";
  public static final String PASSWORD_RESET_REQUESTED = "user.password_reset_requested";
  public static final String GOOGLE_ACCOUNT_LINKED = "user.google_account_linked";

  private UserEventTypes() {}
}

