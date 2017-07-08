package amadeuslms.amadeus.lists;

import java.util.List;

import amadeuslms.amadeus.models.UserModel;

/**
 * Created by zambom on 07/07/17.
 */

public class ParticipantsList {

    private List<UserModel> participants;

    public List<UserModel> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserModel> participants) {
        this.participants = participants;
    }
}
