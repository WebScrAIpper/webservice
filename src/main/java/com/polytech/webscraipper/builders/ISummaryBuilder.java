package com.polytech.webscraipper.builders;

import com.polytech.webscraipper.dto.DocumentDto;
import com.polytech.webscraipper.exceptions.DocumentException;
import com.polytech.webscraipper.exceptions.ScrappingException;
import com.polytech.webscraipper.sdk.responses.PromptResponse;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * This interface contains the methods required to add an alternative pipeline for a specific
 * website. <br>
 * All the implementations of this interface should be annotated with {@link Component}. Otherwise,
 * the builder will not be loaded into the main system.
 */
@Component
public interface ISummaryBuilder {

  /**
   * The method has to implement the pre-processing of the page content before the AI intervention
   * occurs. It can be scrapping some precise elements or getting rid of the ads, anything that
   * could improve the AI performance.
   *
   * @param url the url of the page
   * @param pageContent the content of the page
   * @return the scrapped content
   * @throws ScrappingException if an error occurs during the scrapping
   */
  String scrapContent(String url, String pageContent) throws ScrappingException;

  /**
   * The method has to implement the generation of the prompt that will be sent to the AI. It's
   * recommanded to use Langfuse Prompt Management Service to handle the prompting.
   *
   * @param scrappedContent the content of the page after scrapping
   * @param classifiers the already existing classifiers name
   * @return the AI answer
   */
  PromptResponse generatePrompt(String scrappedContent, List<String> classifiers);

  /**
   * The method has to implement the post-processing of the AI answer. Should do some checks to
   * ensure the answer is relevant and usable and return the final document.
   *
   * @param url the url of the page
   * @param response the AI answer
   * @return the final document.
   * @throws DocumentException if the document is not acceptable.
   */
  DocumentDto polishAnswer(String url, DocumentDto response) throws DocumentException;

  /**
   * The method has to implement the verification of the url to determine if the builder is
   * appropriate for the page.
   *
   * @param url the url of the page
   * @return true if the builder is appropriate, false otherwise
   */
  boolean isAnAppropriateBuilder(String url);
}
