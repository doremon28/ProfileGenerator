package org.log.parser.profile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class UserProfile {
    private String emailProfile;
    private Map.Entry<String, Integer> readingOperations;
    private Map<String, Integer> writingOperations;
    private Map<String, Double> productPrices;

}
