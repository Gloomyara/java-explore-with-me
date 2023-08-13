package ru.practicum.constants;

public class UtilConstants {

    /**
     * Utility constants
     */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String FROM = "0";
    public static final String SIZE = "10";
    /**
     * Stats-controller constants
     */
    public static final String STATS_PATH = "/stats";
    public static final String HIT_PATH = "/hit";
    /**
     Users constants
     */
    public static final String USERS_PATH = "/users";
    public static final String USERS_ID_VAR = "/{userId}";
    /**
     Admin constants
     */
    public static final String ADMIN_PATH = "/admin";
    /**
     Category-controller constants
    */
    public static final String CATEGORY_PATH = "/categories";
    public static final String CATEGORY_ID_VAR = "/{catId}";
    /**
     Compilation-controller constants
     */
    public static final String COMPILATION_PATH = "/compilations";
    public static final String COMPILATION_ID_VAR = "/{compId}";
    /**
     Event-controller constants
     */
    public static final String EVENT_PATH = "/events";
    public static final String EVENT_ID_VAR = "/{eventId}";
    public static final int ADMIN_TIME_RANGE_LIMIT = 1;
    public static final int USER_TIME_RANGE_LIMIT = 2;
    /**
     Request-controller constants
     */
    public static final String REQUEST_PATH = "/requests";
}
