package org.log.parser.shared.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class LogModel {
    private String date;
    private String time;
    private String emailProfile;
    private String level;
    private String thread;
    private String pathClass;
    private String message;
}
