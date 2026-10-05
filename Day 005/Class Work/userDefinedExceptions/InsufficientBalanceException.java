package userDefinedExceptions;

public class InsufficientBalanceException extends Exception{

	InsufficientBalanceException(String message) {
		super(message);
	}

}
