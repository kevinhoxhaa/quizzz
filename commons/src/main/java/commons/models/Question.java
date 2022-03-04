package commons.models;

import commons.utils.QuestionType;

import java.util.Objects;

public abstract class Question {
    protected QuestionType type;

    public Question(QuestionType type) {
        this.type = type;
    }

    public abstract long getPoints();

    public QuestionType getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("<Question: type=%s>", type.toString());
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
