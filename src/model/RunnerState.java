package model;

public enum RunnerState {

    PLAYING,
    PAUSED,
    STOPPED;

    public boolean active() {
        return (this == PLAYING || this == PAUSED);
    }
}
