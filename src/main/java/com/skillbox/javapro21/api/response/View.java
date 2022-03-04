package com.skillbox.javapro21.api.response;

public class View {
    public interface Public {
    }

    public interface Auth extends Public {
    }

    public interface Profile extends Public {
    }

    public interface Friends extends Public {
    }

    public interface Search extends Friends {
    }

    public interface Posts extends Public {
    }

    public interface Dialogs extends Posts {
    }
}
