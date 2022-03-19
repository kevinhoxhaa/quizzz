package commons.entities;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@MappedSuperclass
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;


    protected User(){
    }

    public String username;
    public Long correctAnswers;
    public Long points = 0L;

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Long getCorrectAnswers() {
        return correctAnswers;
    }

    public Long getPoints() {
        return points;
    }

    public Integer getTotalAnswers() {
        return totalAnswers;
    }

    public Boolean getLastAnswerCorrect() {
        return lastAnswerCorrect;
    }

    public Integer totalAnswers;
    public Boolean lastAnswerCorrect;

    public User(String username) {
        this.username = username;
        this.correctAnswers = Long.valueOf(0);
        this.points = 0L;
        this.totalAnswers = 0;
        this.lastAnswerCorrect = false;
    }

    /**
     * A method that increments the user's score
     * @param score
     */
    public void incrementScore(long score){
        points+=score;
    }

    /**
     * Resets the user's score when game restarts
     */
    public void resetScore(){
        points=0L;
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