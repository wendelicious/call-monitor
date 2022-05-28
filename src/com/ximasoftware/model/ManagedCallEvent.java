package com.ximasoftware.model;

import com.ximasoftware.EventType;
import com.ximasoftware.event.CallEvent;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.ximasoftware.util.Assertion.firstNonNull;

public class ManagedCallEvent {
    private final CallEvent event;
    private final ManagedCall call;
    private volatile OffsetDateTime nextEventAt;

    ManagedCallEvent(ManagedCall call, CallEvent event) {
        this.call = call;
        this.event = event;
    }

    public ManagedCallEvent endedBy(ManagedCallEvent next) {
        nextEventAt = next.event.getWhen();
        return this;
    }

    public CallEvent getEvent() {
        return event;
    }

    public ManagedCall getCall() {
        return call;
    }


    public OffsetDateTime getNextEventAt() {
        return nextEventAt;
    }

    public Duration getDuration() {
        if (event.getEventType() == EventType.DROP) {
            return Duration.ZERO;
        }

        final OffsetDateTime end = firstNonNull(nextEventAt, OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC));
        return Duration.between(event.getWhen(), end);
    }
}
