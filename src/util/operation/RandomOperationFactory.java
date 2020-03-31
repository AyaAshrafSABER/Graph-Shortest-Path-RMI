package util.operation;
import util.operation.Operation.Type;
import java.security.InvalidParameterException;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.abs;

public class RandomOperationFactory {
    private double pQuery, pAdd, pDel;
    private int minOp, maxOp;

    public RandomOperationFactory(double pQuery, double pAdd, double pDel, int minOp, int maxOp) {
        this.pQuery = pQuery;
        this.pAdd = pAdd;
        this.pDel = pDel;
        this.minOp = minOp;
        this.maxOp = maxOp;

        if (abs(pQuery + pAdd + pDel - 1) > 1E-6) {
            throw new InvalidParameterException("Type probabilities don't sum to 1");
        }
    }

    public Operation getOperation() {
        Type type = randomType();
        int op1 = randomOperand();
        int op2 = randomOperandExcept(op1);
        return new Operation(type, op1, op2);
    }

    private Type randomType() {
        double r = Math.random();

        if (r < pQuery) return Type.QUERY;
        else if (r < pQuery + pAdd) return Type.ADD;
        else return Type.DEL;
    }

    private int randomOperand() {
        return ThreadLocalRandom.current().nextInt(minOp, maxOp + 1);
    }

    private int randomOperandExcept(int operand) {
        int r = ThreadLocalRandom.current().nextInt(minOp, maxOp + 1);
        while (r == operand) {
            r = ThreadLocalRandom.current().nextInt(minOp, maxOp + 1);
        }
        return r;
    }
}
