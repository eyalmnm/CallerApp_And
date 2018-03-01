package com.em_projects.callerapp.models;

/**
 * Created by eyalmuchtar on 3/1/18.
 */

public class CallCounterPair<k, v> {

    private k key;
    private v value;

    public CallCounterPair(k key, v value) {
        this.key = key;
        this.value = value;
    }

    public k getKey() {
        return key;
    }

    public v getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "CallCounterPair{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallCounterPair<?, ?> that = (CallCounterPair<?, ?>) o;

        if (!key.equals(that.key)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
