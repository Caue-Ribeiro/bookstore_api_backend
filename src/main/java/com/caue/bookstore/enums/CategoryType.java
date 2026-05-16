package com.caue.bookstore.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CategoryType {
    FANTASY,
    ROMANCE,
    HORROR,
    ADVENTURE,
    HISTORY,
    SUSPENSE,
    PHILOSOPHY,
    DIDACTIC,
    SCIFI,
    MYSTERY,
    BIOGRAPHY,
    POETRY,
    CLASSICS,
    DRAMA,
    CONTEMPORARY,
    TECHNOLOGY,
    PSYCHOLOGY,
    ECONOMICS,
    SCIENCE,
    @JsonProperty("RELIGION AND SPIRITUALITY")
    RELIGION_AND_SPIRITUALITY,
    @JsonProperty("SELF-HELP")
    SELF_HELP,
    THRILLER
}
