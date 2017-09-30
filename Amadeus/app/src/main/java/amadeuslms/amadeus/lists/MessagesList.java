package amadeuslms.amadeus.lists;

import java.util.ArrayList;
import java.util.List;

import amadeuslms.amadeus.models.MessageModel;
import amadeuslms.amadeus.models.UserModel;

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

    public List<MessageModel> getMy_messages(UserModel user) {

        List<MessageModel> myMsg_filter = new ArrayList<MessageModel>();

        for (int i = 0; i < messages.size(); i++) {
            if(messages.get(i).getUser().getEmail().equals(user.getEmail())) {
                MessageModel mm = new MessageModel();
                mm.setText(messages.get(i).getText());
                mm.setUser(messages.get(i).getUser());
                mm.setSubject(messages.get(i).getSubject());
                mm.setCreate_date(messages.get(i).getCreate_date());
                mm.setImage_url(messages.get(i).getImage_url());
                myMsg_filter.add(mm);
            }
        }

        return myMsg_filter;
    }
}
