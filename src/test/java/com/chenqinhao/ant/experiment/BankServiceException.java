package com.chenqinhao.ant.experiment;

public class BankServiceException extends Exception {
	public BankServiceException(String aMessage) {
        super(aMessage);
    }

    public BankServiceException(String aMessage, Throwable aCause) {
        super(aMessage, aCause);
    }
}
