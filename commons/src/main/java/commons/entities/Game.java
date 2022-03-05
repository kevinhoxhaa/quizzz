package commons.entities;

import commons.models.Question;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    public List<Long> userIds;
    public Question nextQuestion;

    @SuppressWarnings("unused")
    private Game() {
        // for object mapper
    }

    /**
     * Constructs a new game object with the ids of the users
     * that participate in that game and the randomly
     * generated next question for that game
     * @param userIds the ids of the users that participate in the game
     * @param nextQuestion the randomly generated next question for that game
     */
    public Game(List<Long> userIds, Question nextQuestion) {
        this.userIds = userIds;
        this.nextQuestion = nextQuestion;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
