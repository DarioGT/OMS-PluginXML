// Autogenerated AST node
package org.python.parser.ast;
import org.python.parser.SimpleNode;
import java.io.DataOutputStream;
import java.io.IOException;

public class Slice extends sliceType {
    public exprType lower;
    public exprType upper;
    public exprType step;

    public Slice(exprType lower, exprType upper, exprType step) {
        this.lower = lower;
        this.upper = upper;
        this.step = step;
    }

    public Slice(exprType lower, exprType upper, exprType step, SimpleNode
    parent) {
        this(lower, upper, step);
        this.beginLine = parent.beginLine;
        this.beginColumn = parent.beginColumn;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("Slice[");
        sb.append("lower=");
        sb.append(dumpThis(this.lower));
        sb.append(", ");
        sb.append("upper=");
        sb.append(dumpThis(this.upper));
        sb.append(", ");
        sb.append("step=");
        sb.append(dumpThis(this.step));
        sb.append("]");
        return sb.toString();
    }

    public void pickle(DataOutputStream ostream) throws IOException {
        pickleThis(45, ostream);
        pickleThis(this.lower, ostream);
        pickleThis(this.upper, ostream);
        pickleThis(this.step, ostream);
    }

    public Object accept(VisitorIF visitor) throws Exception {
        return visitor.visitSlice(this);
    }

    public void traverse(VisitorIF visitor) throws Exception {
        if (lower != null)
            lower.accept(visitor);
        if (upper != null)
            upper.accept(visitor);
        if (step != null)
            step.accept(visitor);
    }

}
