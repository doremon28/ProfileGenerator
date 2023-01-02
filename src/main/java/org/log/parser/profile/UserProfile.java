package org.log.parser.profile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class UserProfile {
    // The email of the user
    private String emailProfile;
    // The number of reading operations of the user
    private Map.Entry<String, Integer> readingOperations;
    // The number of writing operations of the user
    private Map<String, Integer> writingOperations;
    // The product that the user has read with there price
    private Map<String, Double> productPrices;

}
