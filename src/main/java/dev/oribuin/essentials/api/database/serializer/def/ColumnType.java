package dev.oribuin.essentials.api.database.serializer.def;

public enum ColumnType {
    BYTE_ARRAY("VARBINARY(2456)"), // byte[]
    CHAR, // char
    TEXT, // String
    BOOLEAN, // boolean
    INTEGER, // int
    FLOAT, // float
    DOUBLE, // double
    LONG, // long
    SHORT, // short
    DATE, // Date
    UUID("VARCHAR(36)"), // UUID
    ;

    private final String realValue;

    ColumnType() {
        this.realValue = this.name();
    }

    ColumnType(String realValue) {
        this.realValue = realValue;
    }

    public String realValue() {
        return this.realValue;
    }

}
