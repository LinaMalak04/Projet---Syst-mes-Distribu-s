package org.example.chatbotservice.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

@Service
public class BankAgent {
    private static final String SYSTEM_MESSAGE = """
            Tu es l'assistant de la banque E-Bank. Tu aides les conseillers à consulter les clients,
            les comptes bancaires et les opérations, et à effectuer des débits, des crédits et des virements.
            Utilise toujours les outils disponibles pour obtenir des données réelles : n'invente jamais
            un identifiant, un solde ou une opération. Si une information manque pour agir (identifiant
            de compte, montant), demande-la. Avant un débit, un crédit ou un virement, rappelle le montant
            et les comptes concernés. Réponds en français, de façon courte et claire, en texte simple.
            """;

    private final ChatClient chatClient;

    public BankAgent(ChatClient.Builder chatClientBuilder, ToolCallbackProvider mcpTools) {
        // Short-term memory : the last 20 messages of each conversation are sent back to the LLM
        ChatMemory chatMemory = MessageWindowChatMemory.builder().maxMessages(20).build();
        this.chatClient = chatClientBuilder
                .defaultSystem(SYSTEM_MESSAGE)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultToolCallbacks(mcpTools)
                .build();
    }

    public String ask(String conversationId, String question) {
        return chatClient.prompt()
                .user(question)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}
