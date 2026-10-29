package org.example.ebankservice.mcp;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfig {

    // Every @Tool method of EbankMcpTools becomes a tool of the MCP server
    @Bean
    public ToolCallbackProvider ebankTools(EbankMcpTools ebankMcpTools) {
        return MethodToolCallbackProvider.builder().toolObjects(ebankMcpTools).build();
    }
}
