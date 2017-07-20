package amadeuslms.amadeus.response;

import amadeuslms.amadeus.lists.MessagesList;

/**
 * Created by zambom on 20/07/17.
 */

public class MessageResponse extends GenericResponse {

    private MessagesList data;

    public MessagesList getData() {
        return data;
    }

    public void setData(MessagesList data) {
        this.data = data;
    }
}
