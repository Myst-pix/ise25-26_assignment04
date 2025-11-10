package de.uhd.campuscoffee.application.exception;

public class OsmImportException extends RuntimeException {
    public OsmImportException(String message) { super(message); }
    public OsmImportException(String message, Throwable cause) { super(message, cause); }
}

