package util.operation;

public class Operation {
//    public enum Type {READ, ADD, DEL;}

    public enum Type {
        QUERY ("Q"), ADD ("A"), DEL ("D");

        private final String name;

        private Type(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    private final Type type;
    private final int op1;
    private final int op2;

    public Operation(Type type, int op1, int op2) {
        this.type = type;
        this.op1 = op1;
        this.op2 = op2;
    }

    public Type getType() {
        return type;
    }


    public int getOp1() {
        return op1;
    }


    public int getOp2() {
        return op2;
    }

    @Override
    public String toString() {
        return String.format("%s %d %d", type.toString(), op1, op2);
    }

}
