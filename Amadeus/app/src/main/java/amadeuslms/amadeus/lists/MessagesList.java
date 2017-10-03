package amadeuslms.amadeus.lists;

import java.util.ArrayList;
import java.util.List;

import amadeuslms.amadeus.models.MessageModel;

/**
 * Created by zambom on 20/07/17.
 */

public class MessagesList {

    private MessageModel message_sent;
    private List<MessageModel> messages;

    public List<MessageModel> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageModel> messages) {
        this.messages = messages;
    }

    public MessageModel getMessage_sent() {
        return message_sent;
    }

    public void setMessage_sent(MessageModel message_sent) {
        this.message_sent = message_sent;
    }
}
