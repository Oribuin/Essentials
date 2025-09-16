package dev.oribuin.essentials.database.serializer.def;

public enum ColumnType {
    BYTE_ARRAY("VARBINARY(2456)"),
    CHAR,
    TEXT,
    BOOLEAN,
    INTEGER,
    FLOAT,
    DOUBLE,
    LONG,
    SHORT,
    DATE,
    BIGDECIMAL("DECIMAL(15,2)"),
    UUID("VARCHAR(36)"),
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
