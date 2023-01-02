package org.log.parser.shared.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Log file model.
 */
@Getter
@Setter
public class LogFileModel {
    /**
     * The Logs.
     */
    private List<LogModel> logs = new ArrayList<>();
    /**
     * The Path.
     */
    private String path;
    /**
     * The Name file.
     */
    private String nameFile;
}
