package commons.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    public String title;
    public String identifier;

    @Column(name = "consumption_in_wh")
    @JsonProperty("consumption_in_wh")
    public long consumption;

    public String source;

    @Column(name = "image_path")
    @JsonProperty("image_path")
    public String imagePath;

    @SuppressWarnings("unused")
    private Activity() {
        // for object mapper
    }

    public Activity(String title, long consumption, String source) {
        this.title = title;
        this.consumption = consumption;
        this.source = source;
    }

    public Activity(String title, long consumption, String source, String imagePath) {
        this.title = title;
        this.consumption = consumption;
        this.source = source;
        this.imagePath = imagePath;
    }

    public Activity(String identifier, String title, long consumption, String source, String imagePath) {
        this(title, consumption, source, imagePath);
        this.identifier = identifier;
    }

    /**
     * Compares the current Activity object with another objects
     * @param obj the object to compare the current object with
     * @return true if the reflections of the two objects are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (! (obj instanceof Activity)) {
            return false;
        }
        Activity that = (Activity) obj;
        return this.title.equals(that.title) &&
                this.consumption == that.consumption;
    }

    /**
     * Generates the hash code of the current Activity object
     * using the HashCodeBuilder
     * @return the question hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(Activity.class, title, consumption);
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

    /**
     * A getter for the identifier
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * A getter for the consumption
     * @return the consumption
     */
    public long getConsumption() {
        return consumption;
    }

    /**
     * A getter for the image path
     * @return the image path
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * A getter for the source
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * A getter for the id
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * A getter for the title
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * A setter for the consumption
     * @param consumption
     */
    public void setConsumption(long consumption) {
        this.consumption = consumption;
    }

    /**
     * A setter for the id
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * A setter for the identifier
     * @param identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * A setter for the image path
     * @param imagePath
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * A setter for the source
     * @param source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * A setter for the title
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }
}

