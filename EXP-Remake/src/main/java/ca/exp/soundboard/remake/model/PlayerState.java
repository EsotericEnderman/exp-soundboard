package ca.exp.soundboard.remake.model;

public enum PlayerState {
    RESETTING, // thread is either setting up for the first time or setting progress back to zero
    READY, // thread is ready to start playback
    PLAYING, // thread is currently playing audio to output
    PAUSED, // thread is not playing, waiting for signal
    WAIT, // thread is not playing, transitioning to waiting state
    WAITING, // thread is not playing, confirmed to be sleeping
    FINISHED; // thread is cleaning up resources and closing down
}
