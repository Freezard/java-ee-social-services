package exceptions;

public class TooManyFailedTriesException extends Exception {
	public TooManyFailedTriesException(String err) {
		super(err);
	}
}
