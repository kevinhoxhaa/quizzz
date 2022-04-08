package commons.models;

import java.util.Objects;

public class Emoji {
    private String imageName;
    private String username;

    /**
     * A private constructor for object mapping
     */
    @SuppressWarnings("unused")
    public Emoji() {
        // for object mapper
    }

    /**
     * Constructs a new emoji object with an image and a corresponding
     * username
     * @param imageName the image name
     * @param username the username
     */
    public Emoji(String imageName, String username) {
        this.imageName = imageName;
        this.username = username;
    }

    /**
     * Returns the image name of the emoji
     * @return the image name
     */
    public String getImageName() {
        return imageName;
    }

    /**
     * Sets the image name of the emoji
     * @param imageName the image name
     */
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    /**
     * Returns the corresponding emoji username
     * @return the corresponding emoji username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the corresponding emoji username
     * @param username the username of the user
     *                 who reacted with this emoji
     */
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Emoji{" +
                "imageName='" + imageName + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Emoji emoji = (Emoji) o;
        return Objects.equals(imageName, emoji.imageName) && Objects.equals(username, emoji.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageName, username);
    }
}
