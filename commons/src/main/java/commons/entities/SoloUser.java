package commons.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="soloUser")
public class SoloUser extends User{

    public SoloUser(String username) {
        super(username);
    }

    @SuppressWarnings("unused")
    private SoloUser() {
        // for object mapper
    }
}
