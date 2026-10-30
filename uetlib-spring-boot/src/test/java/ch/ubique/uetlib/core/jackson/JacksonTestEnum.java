package ch.ubique.uetlib.core.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum JacksonTestEnum {
    @JsonProperty("option_a")
    OPTION_A,
    @JsonProperty("option_b")
    OPTION_B
}
