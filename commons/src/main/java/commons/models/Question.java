package commons.models;

import commons.utils.QuestionType;

import java.util.Objects;

public abstract class Question {
    protected QuestionType type;
    protected long seconds;

    /**
     * Constructs a question object with a given type
     * @param type the type of the question, as described
     *             in the QuestionType enum docs
     */
    public Question(QuestionType type) {
        this.type = type;
        this.seconds = 1;
    }

    /**
     * Calculates the points for a given answer to the question
     * based on the question type
     * @return points for the question
     */
    public abstract long getPoints();

    /**
     * Returns the question type
     * @return the question type
     */
    public QuestionType getType() {
        return type;
    }

    /**
     * The time it took the user to answer the question
     * in seconds
     * @return time in seconds
     */
    public long getSeconds() {
        return seconds;
    }

    /**
     * Returns true if the answer the user has chosen is correct
     * @return true if the answer chosen by the user is the correct one
     */
    public abstract boolean hasCorrectUserAnswer();

    /**
     * Generates a string representation of the given object
     * @return a string representation of the given object
     */
    @Override
    public String toString() {
        return "Question{" +
                "type=" + type +
                '}';
    }

    /**
     * Checks whether another object is of the same instance
     * and has fields equal to the given object
     * @param obj the object to compare the current object with
     * @return true of the two objects are the same or are of
     * the same instance and have equal fields
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }

        if(obj instanceof Question) {
            return ((Question) obj).type.equals(type);
        }

        return false;
    }

    /**
     * Generates a hash code for the given object based
     * on its fields
     * @return object hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
