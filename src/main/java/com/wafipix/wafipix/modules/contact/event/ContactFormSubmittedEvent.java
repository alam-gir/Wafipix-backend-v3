package com.wafipix.wafipix.modules.contact.event;

import com.wafipix.wafipix.modules.contact.entity.Contact;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ContactFormSubmittedEvent extends ApplicationEvent {
    
    private final Contact contact;
    
    public ContactFormSubmittedEvent(Object source, Contact contact) {
        super(source);
        this.contact = contact;
    }
}
