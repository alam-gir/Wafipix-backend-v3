package com.wafipix.wafipix.modules.contact.event;

import com.wafipix.wafipix.modules.contact.entity.Contact;
import com.wafipix.wafipix.modules.contact.entity.ContactReply;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ContactReplySentEvent extends ApplicationEvent {
    
    private final Contact contact;
    private final ContactReply reply;
    
    public ContactReplySentEvent(Object source, Contact contact, ContactReply reply) {
        super(source);
        this.contact = contact;
        this.reply = reply;
    }
}
