package com.ariel.guardian.command;

import com.ariel.guardian.command.params.Params;
import com.ariel.guardian.library.database.model.WrapperMessage;

/**
 * Created by mikalackis on 6.7.16..
 */
public class Command<T extends Params> {

    protected WrapperMessage message = null;

    protected Command next = null;

    protected T params = null;

    protected Command(AbstractBuilder builder){
        message = builder.message;
        next = builder.next;
        params = (T)builder.params;
    }

    public void setWrapperMessage(final WrapperMessage message){
        this.message = message;
    }

    protected void execute(){
        // to override
    }

    protected void onNextCommand(){
        next.execute();
    }

    //public abstract void injectComponent(final GuardianComponent component);

    protected void reportToMaster(){
        // override in concrete command implementation
    }

    public static abstract class AbstractBuilder<BuilderT extends AbstractBuilder<BuilderT, ParamsT>, ParamsT extends Params> {

        private Command next;
        private ParamsT params;
        private WrapperMessage message;

        protected abstract BuilderT me();

        public abstract Command build();

        public BuilderT nextCommand(final Command next) {
            this.next = next;
            return me();
        }

        public BuilderT withParams(final ParamsT params){
            this.params = params;
            return me();
        }

        public BuilderT withMessage(final WrapperMessage message){
            this.message = message;
            return me();
        }

    }

}
