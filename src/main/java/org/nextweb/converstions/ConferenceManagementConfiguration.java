package org.nextweb.converstions;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"org.nextweb.conversations.controller", "org.nextweb.conversations.security"}) // Scan these packages
public class ConferenceManagementConfiguration {

}
