package org.log.parser;

import org.apache.commons.cli.*;
import org.log.parser.shared.LogFileReader;
import org.log.parser.shared.constants.OptionsCli;
import org.log.parser.shared.utils.ConfigOptionsParam;
import org.slf4j.Logger;

public class Main {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(Main.class);
    private static String sourceLogProfile;
    private static String outputFileLogProfile;

    public static void main(String[] args) {
        LOGGER.info("Start the program");
        configurationCli(args);
        LOGGER.info("sourceLogProfile: {}", sourceLogProfile);
        LOGGER.info("outputFileLogProfile: {}", outputFileLogProfile);
        LogFileReader logFileReader = new LogFileReader(sourceLogProfile, outputFileLogProfile);
        logFileReader.readLogFile();
        logFileReader.writeJsonFileUserProfile();
        LOGGER.info("End the program size {}", logFileReader.getLogFileModel().getLogs().size());
    }

    private static void configurationCli(String[] args) {
        LOGGER.info("Start the configuration of the CLI");
        ConfigOptionsParam configOptionsParam = new ConfigOptionsParam();
        try {
            CommandLineParser commandLineParser = new DefaultParser();
            CommandLine commandLine = commandLineParser.parse(configOptionsParam.getOptions(), args);
            sourceLogProfile = commandLine.getOptionValue(OptionsCli.SOURCE_LOG_PATH);
            outputFileLogProfile = commandLine.getOptionValue(OptionsCli.OUTPUT_LOG_PROFILE);
            if (outputFileLogProfile != null) {
                OptionsCli.PROFILE_OUTPUT_RESULT = outputFileLogProfile;
            }
            boolean helpMode = commandLine.hasOption(OptionsCli.HELP);// args.length == 0
            if (helpMode || sourceLogProfile == null) {
                final HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("Spoon-Logging", configOptionsParam.getOptions(), true);
                System.exit(0);
            }

        } catch (ParseException e) {
            LOGGER.error("Error while parsing the command line arguments : {}", e.getMessage());
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Spoon-Logging", configOptionsParam.getOptions(), true);
            System.exit(0);
        }
    }
}