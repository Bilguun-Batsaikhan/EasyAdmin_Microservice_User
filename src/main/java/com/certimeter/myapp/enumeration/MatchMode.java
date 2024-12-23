package com.certimeter.myapp.enumeration;

// This could be in a common shared library
public enum MatchMode {
    STARTS_WITH,
    CONTAINS,
    NOT_CONTAINS,
    ENDS_WITH,
    EQUALS,
    NOT_EQUALS,
    DATE_BEFORE, DATE_AFTER, DATE_IS_NOT, DATE_IS, NO_FILTER
}
