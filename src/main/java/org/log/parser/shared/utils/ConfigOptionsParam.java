package org.log.parser.shared.utils;


import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.log.parser.shared.constants.OptionsCli;

public class ConfigOptionsParam {
    private final Option sourceLogProfile;
    private final Option outputFileLogProfile;

    private final Option help;
    private  final Options options;

    public Options getOptions() {
        return options;
    }

    public ConfigOptionsParam() {
        this.help = Option.builder(OptionsCli.HELP_SHORT)
                .longOpt(OptionsCli.HELP)
                .desc(OptionsCli.HELP_DESCRIPTION)
                .build();
        this.sourceLogProfile = Option.builder(OptionsCli.SOURCE_LOG_PATH_SHORT)
                .longOpt(OptionsCli.SOURCE_LOG_PATH)
                .hasArg()
                .desc(OptionsCli.SOURCE_LOG_PATH_DESCRIPTION)
                .required(true)
                .build();
        this.outputFileLogProfile = Option.builder(OptionsCli.OUTPUT_LOG_PROFILE_SHORT)
                .longOpt(OptionsCli.OUTPUT_LOG_PROFILE)
                .hasArg()
                .desc(OptionsCli.OUTPUT_LOG_PROFILE_DESCRIPTION)
                .build();
        this.options = new Options();
        this.options.addOption(help);
        this.options.addOption(sourceLogProfile);
        this.options.addOption(outputFileLogProfile);

    }
}
