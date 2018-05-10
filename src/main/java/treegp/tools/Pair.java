package treegp.tools;

public class Pair<T> {
    private final T v1;
    private final T v2;

    public Pair(T v1, T v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public T _1() {
        return v1;
    }

    public T _2() {
        return v2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Pair<?> tupleTwo = (Pair<?>) o;

        return (v1 != null ? v1.equals(tupleTwo.v1) : tupleTwo.v1 == null)
                && (v2 != null ? v2.equals(tupleTwo.v2) : tupleTwo.v2 == null);
    }

    @Override
    public int hashCode() {
        int result = v1 != null ? v1.hashCode() : 0;
        result = 31 * result + (v2 != null ? v2.hashCode() : 0);
        return result;
    }
}
