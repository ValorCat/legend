package legend.compiletime.expression.value;

import java.util.List;
import java.util.StringJoiner;

/**
 * @since 12/24/2018
 */
public class LList extends Value {

    private List<Value> list;

    public LList(List<Value> list) {
        super("list");
        this.list = list;
    }

    @Override
    public boolean matches(String pattern) {
        return asString().equals(pattern);
    }

    @Override
    public String asString() {
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (Value element : list) {
            joiner.add(element.asString());
        }
        return joiner.toString();
    }

    @Override
    public List<Value> asList() {
        return list;
    }

    @Override
    public boolean equals(Value other) {
        if (type() != other.type()) {
            return false;
        }
        List<Value> otherList = ((LList) other).list;
        if (list.size() != otherList.size()) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).equals(otherList.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return list.toString();
    }
}
