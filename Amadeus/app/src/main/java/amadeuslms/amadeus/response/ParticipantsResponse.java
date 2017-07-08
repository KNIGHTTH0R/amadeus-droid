package amadeuslms.amadeus.response;

import amadeuslms.amadeus.lists.ParticipantsList;

/**
 * Created by zambom on 07/07/17.
 */

public class ParticipantsResponse extends GenericResponse {

    private ParticipantsList data;

    public ParticipantsList getData() {
        return data;
    }

    public void setData(ParticipantsList data) {
        this.data = data;
    }
}
