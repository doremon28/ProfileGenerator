package org.log.parser.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.log.parser.profile.UserProfile;
import org.log.parser.shared.models.LogFileModel;
import org.log.parser.shared.models.LogModel;
import org.slf4j.Logger;

import java.io.*;
import java.util.*;


/**
 * The type Log file reader.
 */
public class LogFileReader {

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LogFileReader.class);
    /**
     * The Log file path.
     */
    private final String LOG_FILE_PATH;
    /**
     * The Log file path output.
     */
    private final String LOG_FILE_PATH_OUTPUT;
    /**
     * The Log file model.
     */
    @Getter
    private final LogFileModel logFileModel = new LogFileModel();
    /**
     * The Log model map.
     */
    @Getter
    private final Map<String, List<LogModel>> logModelMap = new HashMap<>();

    /**
     * The User profiles.
     */
    @Getter
    private final List<UserProfile> userProfiles = new ArrayList<>();

    /**
     * Instantiates a new Log file reader.
     *
     * @param logFilePath       the log file path
     * @param logFilePathOutput the log file path output
     */
    public LogFileReader(String logFilePath, String logFilePathOutput) {
        LOG_FILE_PATH = logFilePath;
        LOG_FILE_PATH_OUTPUT = logFilePathOutput;
    }

    /**
     * Read log file.
     */
    public void readLogFile() {
        LOGGER.info("Start the reading of the log file");
        File file = new File(LOG_FILE_PATH);
        if (file.exists()) {
            LOGGER.warn("The file {} exists", LOG_FILE_PATH);
            logFileModel.setPath(file.getParent());
            logFileModel.setNameFile(file.getName());
        } else {
            LOGGER.error("The file {} does not exist", LOG_FILE_PATH);
            System.exit(1);
        }
        BufferedReader bufferedReader = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(LOG_FILE_PATH);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String startLine;
            while ((startLine = bufferedReader.readLine()) != null) {
                parseLogLine(startLine);
            }
            writeProfilesMap();
            buildProfiles();
        } catch (FileNotFoundException e) {
            LOGGER.error("Error while reading the log file from FileNotFoundException : {}", e.getMessage());
        } catch (IOException e) {
            LOGGER.error("Error while reading the log file from IOException : {}", e.getMessage());
        } finally {
            LOGGER.info("End the reading of the log file");
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                LOGGER.error("Error while closing the bufferedReader : {}", e.getMessage());
            }
        }
    }

    /**
     * Parse log line.
     *
     * @param line the line
     */
    private void parseLogLine(String line) {
        List<String> parts = Arrays.stream(line.split("---")).toList();
        List<String> parts1 = Arrays.stream(parts.get(0).split(" ")).filter(s -> !s.isEmpty()).toList();
        List<String> parts2 = Arrays.stream(parts.get(1).split(":",2)).filter(s -> !s.isEmpty()).toList();
        List<String> parts3 = Arrays.stream(parts2.get(0).split("]")).filter(s -> !s.isEmpty()).toList();
        String date = "";
        String time = "";
        String emailProfile = "";
        String level = "";
        String thread = "";
        String pathClass = "";
        String message = "";
        if (parts1.size() == 4) {
            date = parts1.get(0);
            time = parts1.get(1);
            emailProfile = parts3.get(0);
            level = parts1.get(2);
            thread = parts3.get(0);
            pathClass = parts3.size() == 2 ? parts3.get(1) : "";
            message = parts2.get(1);
        } else if (parts1.size() == 5) {
            date = parts1.get(0);
            time = parts1.get(1);
            emailProfile = parts1.get(2);
            level = parts1.get(3);
            thread = parts3.get(0);
            pathClass = parts3.get(1);
            message = parts2.get(1);
        }
        LogModel logModel = LogModel.builder()
                .date(date)
                .time(time)
                .emailProfile(emailProfile)
                .level(level)
                .thread(thread)
                .pathClass(pathClass)
                .message(message)
                .build();
        logFileModel.getLogs().add(logModel);
    }

    /**
     * Build profiles.
     */
    private void buildProfiles() {
        for(Map.Entry<String, List<LogModel>> entry : logModelMap.entrySet()) {
            UserProfile userProfile = new UserProfile();
            userProfile.setEmailProfile(entry.getKey());
            List<LogModel> value = entry.getValue();
            Map.Entry<String, Integer> readingOperation = filterReadingLogs(value);
            userProfile.setReadingOperations(readingOperation);
            Map<String, Integer> writingOperation = filterWritingLogs(value);
            userProfile.setWritingOperations(writingOperation);
            Map<String, Double> productPrices = filterProductPrices(value);
            userProfile.setProductPrices(productPrices);
            userProfiles.add(userProfile);
        }
    }

    /**
     * Filter product prices map.
     *
     * @param value the value
     * @return the map
     */
    private Map<String, Double> filterProductPrices(List<LogModel> value) {
        Map<String, Double> productPrices = new HashMap<>();
        for (LogModel logModel : value) {
            if (logModel.getMessage().contains("product called")) {
                String[] parts = logModel.getMessage().split("price");
                String productName = parts[0].split("product called")[1].trim();
                String productPrice = parts[1];
                productPrices.put(productName, Double.parseDouble(productPrice));
            }
        }
        return productPrices;
    }

    /**
     * Filter writing logs map.
     *
     * @param values the values
     * @return the map
     */
    private Map<String, Integer> filterWritingLogs(List<LogModel> values) {
        Map<String, Integer> writingOperation = new HashMap<>();
        for (LogModel logModel : values) {
            switch (logModel.getMessage()) {
                case " createProduct method called" ->
                        writingOperation.put("createProduct", writingOperation.getOrDefault("createProduct", 0) + 1);
                case " updateProduct method called" ->
                        writingOperation.put("updateProduct", writingOperation.getOrDefault("updateProduct", 0) + 1);
                default -> {}
            }
        }
        return writingOperation;
    }

    /**
     * Filter reading logs map . entry.
     *
     * @param logs the logs
     * @return the map . entry
     */
    private Map.Entry<String, Integer> filterReadingLogs(List<LogModel> logs) {
        Map.Entry<String, Integer> entry = null;
        List<String> list = new ArrayList<>();
        for (LogModel logModel : logs) {
           if (logModel.getMessage().equals(" getProductById method called")) {
               list.add(logModel.getMessage());
           }
        }

        if (list.isEmpty()) {
            return Map.entry(" getProduct method called", 0);
        } else {
            entry = Map.entry(list.get(0), list.size());
        }

        return entry;
    }

    /**
     * Write profiles map.
     */
    private void writeProfilesMap() {
        logFileModel.getLogs()
                .stream()
                .filter(logModel -> logModel.getEmailProfile().contains("@gmail.com"))
                .forEach(logModel -> {
                    logModelMap.putIfAbsent(logModel.getEmailProfile(), new ArrayList<>());
                    logModelMap.get(logModel.getEmailProfile()).add(logModel);
                });

    }

    /**
     * Print for loop.
     *
     * @param parts the parts
     */
    private void printForLoop(String[] parts) {
        for (int i = 0; i < parts.length; i++) {
            System.out.println("parts"+0+"[" + i + "] = " + parts[i]);
        }
    }

    /**
     * Gets highest reading operations.
     *
     * @return the highest reading operations
     */
    public UserProfile getHighestReadingOperations() {
        return userProfiles.stream()
                .max(Comparator.comparingInt(userProfile -> userProfile.getReadingOperations() != null ? userProfile.getReadingOperations().getValue() : 0))
                .orElse(null);
    }

    /**
     * Gets highest writing operations.
     *
     * @return the highest writing operations
     */
    public UserProfile getHighestWritingOperations() {
        return userProfiles.stream()
                .max(Comparator.comparingInt(userProfile -> userProfile.getWritingOperations().values().stream().mapToInt(Integer::intValue).sum()))
                .orElse(null);
    }

    /**
     * Gets highest product prices.
     *
     * @return the highest product prices
     */
    public UserProfile getHighestProductPrices() {
        return userProfiles.stream()
                .max(Comparator.comparingDouble(userProfile -> userProfile.getProductPrices().values().stream().mapToDouble(Double::doubleValue).sum()))
                .orElse(null);
    }

    /**
     * Write json file user profile.
     */
    public void writeJsonFileUserProfile() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            if (LOG_FILE_PATH_OUTPUT != null) {
                objectMapper.writeValue(new File(LOG_FILE_PATH_OUTPUT+File.separator+"userProfile.json"), userProfiles);
                objectMapper.writeValue(new File(LOG_FILE_PATH_OUTPUT+File.separator+"userProfileWithHighestReadingOperations.json"), getHighestReadingOperations());
                objectMapper.writeValue(new File(LOG_FILE_PATH_OUTPUT+File.separator+"userProfileWithHighestWritingOperations.json"), getHighestWritingOperations());
                objectMapper.writeValue(new File(LOG_FILE_PATH_OUTPUT+File.separator+"userProfileWithHighestProductPrices.json"), getHighestProductPrices());
            } else {
                objectMapper.writeValue(new File("userProfile.json"), userProfiles);
                objectMapper.writeValue(new File("userProfileWithHighestReadingOperations.json"), getHighestReadingOperations());
                objectMapper.writeValue(new File("userProfileWithHighestWritingOperations.json"), getHighestWritingOperations());
                objectMapper.writeValue(new File("userProfileWithHighestProductPrices.json"), getHighestProductPrices());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
