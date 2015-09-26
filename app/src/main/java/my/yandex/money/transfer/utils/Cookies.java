package my.yandex.money.transfer.utils;

public final class Cookies {
    private static final String LOGIN_COOKIE_NAME = "yandex_login";
    private static final String COOKIE_SEPARATOR = ";";

    private Cookies() { /* */ }

    public static String getLogin(String cookies) {
        if (cookies == null) return "";

        int loginStart = cookies.indexOf(LOGIN_COOKIE_NAME);
        if (loginStart < 0) {
            return "";
        } else {
            loginStart += LOGIN_COOKIE_NAME.length() + 1;
        }

        int loginEnd = cookies.indexOf(COOKIE_SEPARATOR, loginStart);
        loginEnd = (loginEnd < 0) ? cookies.length() : loginEnd;

        return cookies.substring(loginStart, loginEnd);
    }
}
