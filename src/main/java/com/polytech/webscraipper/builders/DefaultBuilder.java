package com.polytech.webscraipper.builders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.polytech.webscraipper.PromptException;
import com.polytech.webscraipper.dto.AIFilledDocument;
import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.sdk.responses.PromptResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;

import java.util.List;

public class DefaultBuilder extends ISummaryBuilder {
    @Override
    public String scrapContent(String pageContent) {
        Document document = Jsoup.parse(pageContent);

        document.select("script, style, form, nav, aside, button, svg").remove();
        // TODO: think about the iframe
        return document.toString();
    }

    @Override
    public PromptResponse generatePrompt(String scrappedContent, List<String> classifiers) {
        return promptManagementService.createDefaultProdPrompt(
                classifiers, scrappedContent);
    }

    @Override
    public String requestAI(PromptResponse prompt) {

        // Might be nice to set up a timeout here
        var aiAnswer = chatModel.call(
                new Prompt(prompt.prompt, OpenAiChatOptions.builder().model("gpt-4o-mini").build())
        );
        return aiAnswer.getResult().getOutput().getText();
    }

    @Override
    public DocumentDto polishAnswer(String url, String response) throws PromptException {
        DocumentDto documentDto;
        try {
            documentDto = objectMapper.readValue(response, DocumentDto.class);
        } catch (JsonProcessingException e) {
            throw new PromptException("The AI response does not match the expected format.");
        }
        documentDto.setUrl(url);
        return documentDto;
    }

    @Override
    public boolean isAnAppropriateBuilder(String url) {
        return true;
    }
}
