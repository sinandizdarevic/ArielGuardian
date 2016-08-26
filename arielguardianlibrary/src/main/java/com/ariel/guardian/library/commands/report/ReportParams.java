package com.ariel.guardian.library.commands.report;

import com.ariel.guardian.library.commands.Params;

/**
 * Created by mikalackis on 18.8.16..
 */
public class ReportParams extends Params {

    // command parameters
    /**
     * Boolean parameter. If provided, location will be reported via SMS too.
     * Not supported at the moment.
     */
    public static final String PARAM_INVOKED_COMMAND = "invoked_command";
    public static final String PARAM_COMMAND_STATUS = "command_status";
    public static final String PARAM_ERROR_MSG = "error_msg";

    private String invokedCommand;
    private String errorMsg;
    private boolean commandStatus;

    public ReportParams(final ReportParamBuilder builder){
        super(ReportParams.class.getSimpleName());
        this.invokedCommand = builder.invokedCommand;
        this.errorMsg = builder.errorMsg;
        this.commandStatus = builder.commandStatus;
    }

    public String getInvokedCommand(){
        return invokedCommand;
    }

    public String getErrorMsg(){
        return errorMsg;
    }

    public boolean getCommandStatus(){
        return commandStatus;
    }

    public static class ReportParamBuilder{

        private String invokedCommand;
        private String errorMsg;
        private boolean commandStatus;

        public ReportParamBuilder invokedCommand(final String invokedCommand){
            this.invokedCommand = invokedCommand;
            return this;
        }

        public ReportParamBuilder commandStatus(final boolean executed){
            this.commandStatus = executed;
            return this;
        }

        public ReportParamBuilder errorMsg(final String errorMsg){
            this.errorMsg = errorMsg;
            return this;
        }

        public ReportParams build(){
            return new ReportParams(this);
        }

    }


}
