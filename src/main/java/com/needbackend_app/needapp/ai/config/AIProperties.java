package com.needbackend_app.needapp.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.ai")
public class AIProperties {
    private HuggingFace huggingface = new HuggingFace();
    private Features features = new Features();

    public static class HuggingFace {
        private String username;
        private String apiToken;
        private Models models = new Models();
        private String apiUrl = "https://api-inference.huggingface.co/models";
        private int timeout = 30;
        private boolean enabled = true;

        public static class Models {
            private String categoryRecommendation = "need-category-recommendation";
            private String chatSupport = "need-chat-support";
            private String serviceDescription = "need-service-description";
            private String semanticSearch = "need-semantic-search";
            private String contentModeration = "need-content-moderation";

            // Getters and setters
            public String getCategoryRecommendation() { return categoryRecommendation; }
            public void setCategoryRecommendation(String categoryRecommendation) { 
                this.categoryRecommendation = categoryRecommendation; 
            }
            
            public String getChatSupport() { return chatSupport; }
            public void setChatSupport(String chatSupport) { 
                this.chatSupport = chatSupport; 
            }
            
            public String getServiceDescription() { return serviceDescription; }
            public void setServiceDescription(String serviceDescription) { 
                this.serviceDescription = serviceDescription; 
            }
            
            public String getSemanticSearch() { return semanticSearch; }
            public void setSemanticSearch(String semanticSearch) { 
                this.semanticSearch = semanticSearch; 
            }
            
            public String getContentModeration() { return contentModeration; }
            public void setContentModeration(String contentModeration) { 
                this.contentModeration = contentModeration; 
            }
        }

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getApiToken() { return apiToken; }
        public void setApiToken(String apiToken) { this.apiToken = apiToken; }
        
        public Models getModels() { return models; }
        public void setModels(Models models) { this.models = models; }
        
        public String getApiUrl() { return apiUrl; }
        public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
        
        public int getTimeout() { return timeout; }
        public void setTimeout(int timeout) { this.timeout = timeout; }
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }

    public static class Features {
        private boolean categoryRecommendation = true;
        private boolean chatSupport = true;
        private boolean contentModeration = true;
        private boolean serviceDescriptionGeneration = true;

        // Getters and setters
        public boolean isCategoryRecommendation() { return categoryRecommendation; }
        public void setCategoryRecommendation(boolean categoryRecommendation) { 
            this.categoryRecommendation = categoryRecommendation; 
        }
        
        public boolean isChatSupport() { return chatSupport; }
        public void setChatSupport(boolean chatSupport) { 
            this.chatSupport = chatSupport; 
        }
        
        public boolean isContentModeration() { return contentModeration; }
        public void setContentModeration(boolean contentModeration) { 
            this.contentModeration = contentModeration; 
        }
        
        public boolean isServiceDescriptionGeneration() { return serviceDescriptionGeneration; }
        public void setServiceDescriptionGeneration(boolean serviceDescriptionGeneration) { 
            this.serviceDescriptionGeneration = serviceDescriptionGeneration; 
        }
    }

    // Main getters and setters
    public HuggingFace getHuggingface() { return huggingface; }
    public void setHuggingface(HuggingFace huggingface) { this.huggingface = huggingface; }
    
    public Features getFeatures() { return features; }
    public void setFeatures(Features features) { this.features = features; }
}