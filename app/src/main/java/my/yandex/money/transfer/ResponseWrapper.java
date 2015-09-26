package my.yandex.money.transfer;

/**
 * The class is used for storing extra hash of the response
 * (depending on the time, not the received value).
 * This allows not to handle the same responses repeatedly
 * (when orientation changes etc.), but to handle identical
 * sequential responses from the server and trigger
 * callbacks expected.
 */
public class ResponseWrapper {
    public final long hash = System.currentTimeMillis();
    public final Object response;

    ResponseWrapper(Object response) {
        this.response = response;
    }
}
