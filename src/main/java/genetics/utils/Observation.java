package genetics.utils;

import java.io.Serializable;

public interface Observation extends Serializable {
    double getInput(int index);

    double getOutput(int index);

    double getPredictedOutput(int index);

    default String getTextInput(int index) {
        return null;
    }

    default String getTextOutput(int index) {
        return null;
    }

    default String getPredictedTextOutput(int index) {
        return null;
    }

    void setOutput(int index, double value);

    void setInput(int index, double value);

    void setPredictedOutput(int index, double value);

    default void setOutput(int index, String value) {

    }

    default void setInput(int index, String value) {

    }

    default void setPredictedOutput(int index, String value) {

    }

    int inputCount();

    int outputCount();

    Serializable getTag();

    void setTag(Serializable obj);
}
