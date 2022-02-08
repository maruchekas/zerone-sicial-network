package com.skillbox.javapro21.api.response;

public class View {
    public interface Public {}
        public interface Me extends Public {}
        public interface Friends extends Public {}
        public interface Feeds extends Public {}
            public interface Dialogs extends Feeds {}
            public interface Wall extends Feeds {}

    //TODO:
    //  1. /search
    //  2. AllData
    //  3. Recheck all get requests
}
