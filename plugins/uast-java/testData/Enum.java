enum Season { WINTER, SPRING, SUMMER, AUTUMN }

enum Colors {
    RED, YELLOW, GREEN;

    enum InnerEnum {

    }
}

enum Direction {
    UP {
        public Direction opposite() { return DOWN; }
    },
    DOWN {
        public Direction opposite() { return UP; }
    };

    public abstract Direction opposite();
}

enum DataType {
    INT(true) {
        public Object parse(String string) { return Integer.valueOf(string); }
    },
    INTEGER(false) {
        public Object parse(String string) { return Integer.valueOf(string); }
    },
    STRING(false) {
        public Object parse(String string) { return string; }
    };

    boolean primitive;
    DataType(boolean primitive) { this.primitive = primitive; }

    public boolean isPrimitive() { return primitive; }
    public abstract Object parse(String string);
}