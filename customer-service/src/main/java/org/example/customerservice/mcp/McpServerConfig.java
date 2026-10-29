package org.example.customerservice.mcp;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfig {

    // Every @Tool method of CustomerMcpTools becomes a tool of the MCP server
    @Bean
    public ToolCallbackProvider customerTools(CustomerMcpTools customerMcpTools) {
        return MethodToolCallbackProvider.builder().toolObjects(customerMcpTools).build();
    }
}
