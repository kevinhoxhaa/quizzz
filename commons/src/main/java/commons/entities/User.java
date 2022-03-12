package commons.entities;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    public String username;
    public Long correctAnswers;
    public Long points;
    public Integer totalAnswers;
    public Boolean lastAnswerCorrect;
    public Boolean soloPlayer;
    public Long gameID;

    @SuppressWarnings("unused")
    private User() {
        // for object mapper
    }

    public User(String username, Boolean soloPlayer) {
        this.username = username;
        this.correctAnswers = Long.valueOf(0);
        this.points = 0L;
        this.totalAnswers = 0;
        this.lastAnswerCorrect = false;
        this.soloPlayer = soloPlayer;
    }

    public Boolean getSoloPlayer() {
        return soloPlayer;
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