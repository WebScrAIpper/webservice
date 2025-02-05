package com.polytech.webscraipper.dto;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public enum SUPPORTED_LANGUAGES {
  ENGLISH,
  FRENCH,
  OTHER
}

// Deserializer

class SupportedLanguagesDeserializer extends JsonDeserializer<SUPPORTED_LANGUAGES> {
  @Override
  public SUPPORTED_LANGUAGES deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JacksonException {
    switch (p.getText().toUpperCase()) {
      case "ENGLISH":
        return SUPPORTED_LANGUAGES.ENGLISH;
      case "FRENCH":
        return SUPPORTED_LANGUAGES.FRENCH;
      default:
        return SUPPORTED_LANGUAGES.OTHER;
    }
  }
}
