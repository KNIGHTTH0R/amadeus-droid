package amadeuslms.amadeus.lists;

import java.util.List;

import amadeuslms.amadeus.models.MessageModel;

/**
 * Created by zambom on 20/07/17.
 */

public class MessagesList {

    private List<MessageModel> messages;

    public List<MessageModel> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageModel> messages) {
        this.messages = messages;
    }
}
