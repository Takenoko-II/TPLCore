package com.gmail.subnokoii78.tplcore.schedule;

public interface Scheduler {
    int runTimeout();

    int runTimeout(long delay);

    void clear(int id);

    void clear();
}
