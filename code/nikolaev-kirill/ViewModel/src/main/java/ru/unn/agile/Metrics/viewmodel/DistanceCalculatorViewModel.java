package ru.unn.agile.Metrics.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import ru.unn.agile.Metrics.model.DistanceCalculator;
import ru.unn.agile.Metrics.model.Metric;

import java.util.ArrayList;
import java.util.List;

public class DistanceCalculatorViewModel {
    public static final String HELP_MESSAGE = "Provide input vectors: decimal or integer numbers "
            + "separated by single whitespaces";
    private final StringProperty result = new SimpleStringProperty();
    private final StringProperty firstVec = new SimpleStringProperty();
    private final StringProperty secondVec = new SimpleStringProperty();
    private final StringProperty statusMessage = new SimpleStringProperty();
    private final BooleanProperty calculateButtonDisabled = new SimpleBooleanProperty();
    private final StringProperty metricName = new SimpleStringProperty();
    private final List<ValueChangeListener> valueChangeListeners = new ArrayList<>();
    private final DistanceCalculator calculator = new DistanceCalculator();

    public DistanceCalculatorViewModel() {
        result.set("");
        firstVec.set("");
        secondVec.set("");
        statusMessage.set(HELP_MESSAGE);
        calculateButtonDisabled.set(true);
        metricName.set("RHO INF");

        final List<StringProperty> fields = new ArrayList<StringProperty>() {
            {
                add(firstVec);
                add(secondVec);
            }
        };

        for (StringProperty field: fields) {
            final ValueChangeListener listener = new ValueChangeListener();
            field.addListener(listener);
            valueChangeListeners.add(listener);
        }
    }

    public StringProperty firstVecProperty() {
        return firstVec;
    }
    public void setFirstVec(final String firstVector) {
        firstVec.set(firstVector);
    }

    public StringProperty secondVecProperty() {
        return secondVec;
    }
    public void setSecondVec(final String secondVector) {
        secondVec.set(secondVector);
    }

    public void setMetric(final String metricName) {
        this.metricName.set(metricName);
    }

    public StringProperty statusMessageProperty() {
        return statusMessage;
    }
    public String getStatusMessage() {
        return statusMessage.get();
    }

    public StringProperty resultProperty() {
        return result;
    }
    public String getResult() {
        return result.get();
    }

    public BooleanProperty calculateButtonDisabledProperty() {
        return calculateButtonDisabled;
    }
    public boolean isCalculateButtonDisabled() {
        return calculateButtonDisabled.get();
    }

    public String getInputStatus() {
        String firstVector = firstVec.get();
        String secondVector = secondVec.get();
        boolean isBadInputFormat = !checkVectorString(firstVector)
                || !checkVectorString(secondVector);
        if (isBadInputFormat) {
            calculateButtonDisabled.set(true);
            return "Bad vector format";
        } else {
            if (firstVector.isEmpty() || secondVector.isEmpty()) {
                calculateButtonDisabled.set(true);
                return HELP_MESSAGE;
            }
            boolean haveDifferentSize = firstVector.split(" ").length
                    != secondVector.split(" ").length;
            if (haveDifferentSize) {
                calculateButtonDisabled.set(true);
                return "Vectors have different size";
            } else {
                calculateButtonDisabled.set(false);
                return "";
            }
        }
    }

    public void calculate() {
        if (calculateButtonDisabled.get()) {
            throw new IllegalStateException("Calculation is disabled: Empty or incorrect input");
        }
        float[] firstVector = parseVector(firstVec.get());
        float[] secondVector = parseVector(secondVec.get());
        Metric metric = parseMetric(metricName.get());
        result.set(Float.toString(calculator.calculateDistance(firstVector, secondVector,
                metric)));
    }

    private float[] parseVector(final String rawVector) {
        float[] vector;
        String[] parts = rawVector.split(" ");
        vector = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            float component = Float.parseFloat(parts[i]);
            vector[i] = component;
        }
        return vector;
    }

    private boolean checkVectorString(final String vectorString) {
        String pattern = "((-?\\d+\\.\\d+|-?\\d+)*\\s)*(-?\\d+\\.\\d+|-?\\d+)";
        return vectorString.isEmpty() || vectorString.matches(pattern);
    }

    private Metric parseMetric(final String metricName) {
        switch (metricName) {
            case "RHO INF":
                return Metric.RHO_INF;
            case "RHO ONE":
                return Metric.RHO_ONE;
            case "RHO TWO":
                return Metric.RHO_TWO;
            case "RHO THREE":
                return Metric.RHO_THREE;
            case "RHO FOUR":
                return Metric.RHO_FOUR;
            default:
                throw new IllegalArgumentException("Invalid metric name");
        }
    }

    private class ValueChangeListener implements ChangeListener<String> {
        @Override
        public void changed(final ObservableValue<? extends String> observable,
                            final String oldValue, final String newValue) {
            statusMessage.set(getInputStatus());
        }
    }
}
