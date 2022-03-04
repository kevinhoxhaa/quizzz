package commons.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    public String title;
    @Column(name = "consumption_in_wh")
    public long consumption;
    public String source;

    @SuppressWarnings("unused")
    private Activity() {
        // for object mapper
    }

    public Activity(String title, long consumption, String source) {
        this.title = title;
        this.consumption = consumption;
        this.source = source;
    }

    /**
     * Compares the current Activity object with another objects
     * @param obj the object to compare the current object with
     * @return true if the reflections of the two objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Generates the hash code of the current Activity object
     * using the HashCodeBuilder
     * @return the question hash code
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Returns a String representation of the current activity
     * object
     * @return a String representation of the current activity
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
