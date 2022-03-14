package commons.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="multiplayerUser")
public class MultiplayerUser extends User{

    public Long gameID;

    @SuppressWarnings("unused")
    private MultiplayerUser() {
        // for object mapper
    }

    public MultiplayerUser(String username) {
        super(username);
    }
}
