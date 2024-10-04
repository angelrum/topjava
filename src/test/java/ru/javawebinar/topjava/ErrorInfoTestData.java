package ru.javawebinar.topjava;

import ru.javawebinar.topjava.util.exception.ErrorInfo;
import ru.javawebinar.topjava.util.exception.ErrorType;

public class ErrorInfoTestData {

    public static final String MEAL_URL = "http://localhost/rest/profile/meals/";

    public static final String USER_URL = "http://localhost/rest/admin/users/";

    public static final String PROFILE_URL = "http://localhost/rest/profile";
    public static final MatcherFactory.Matcher<ErrorInfo> ERROR_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(ErrorInfo.class);

    public static final ErrorInfo errorMeal1 = new ErrorInfo(MEAL_URL, ErrorType.VALIDATION_ERROR, "[description] must not be blank");

    public static final ErrorInfo errorUser1 = new ErrorInfo(USER_URL, ErrorType.VALIDATION_ERROR, "[email] must not be blank");

    public static final ErrorInfo errorUserEmail = new ErrorInfo(USER_URL, ErrorType.DATA_ERROR, "User with this email already exists");

    public static final ErrorInfo errorProfileEmail = new ErrorInfo(PROFILE_URL, ErrorType.DATA_ERROR, "User with this email already exists");
}
