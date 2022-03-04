package commons.models;

import commons.utils.QuestionType;

import java.util.Objects;

public abstract class Question {
    protected QuestionType type;

    /**
     * Constructs a question object with a given type
     * @param type the type of the question, as described
     *             in the QuestionType enum docs
     */
    public Question(QuestionType type) {
        this.type = type;
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
