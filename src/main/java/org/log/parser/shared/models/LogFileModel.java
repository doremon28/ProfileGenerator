package org.log.parser.shared.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LogFileModel {
    private List<LogModel> logs = new ArrayList<>();
    private String path;
    private String nameFile;
}
