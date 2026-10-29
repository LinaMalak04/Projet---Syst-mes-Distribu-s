package org.example.chatbotservice.web;

import org.example.chatbotservice.agents.BankAgent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatRestController {
    private final BankAgent bankAgent;

    public ChatRestController(BankAgent bankAgent) {
        this.bankAgent = bankAgent;
    }

    @GetMapping(value = "/chat", produces = "text/plain;charset=UTF-8")
    public String chat(@RequestParam String query,
                       @RequestParam(defaultValue = "web") String conversationId) {
        return bankAgent.ask(conversationId, query);
    }
}
