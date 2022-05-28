package com.ximasoftware.model;

import com.ximasoftware.EventType;
import com.ximasoftware.event.CallEvent;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.ximasoftware.util.Assertion.checkArgument;
import static com.ximasoftware.util.Assertion.firstNonNull;

public class ManagedCall {
    private final String callId;
    private final OffsetDateTime started;
    private volatile OffsetDateTime finished;
    private final String callingParty;
    private final String receivingParty;
    private final List<ManagedCallEvent> events = new CopyOnWriteArrayList<>();

    public ManagedCall(CallEvent dial) {
        checkArgument(dial.getEventType() == EventType.DIAL, "Only dial events create new calls");

        callId = dial.getCallId();
        started = dial.getWhen();
        callingParty = dial.getCallingParty();
        receivingParty = dial.getReceivingParty();

        somethingHappened(dial);
    }

    public List<ManagedCallEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public String getCallId() {
        return callId;
    }

    public String getCallingParty() {
        return callingParty;
    }

    public String getReceivingParty() {
        return receivingParty;
    }

    public OffsetDateTime getStarted() {
        return started;
    }

    public OffsetDateTime getFinished() {
        return finished;
    }

    public ManagedCallEvent somethingHappened(CallEvent event) {
        if (event.getEventType() == EventType.DROP) {
            if (finished != null) {
                throw new IllegalStateException("cannot drop a call that has already ended");
            }
            finished = event.getWhen();
        }

        final ManagedCallEvent callEvent = new ManagedCallEvent(this, event);

        if (events.size() > 0) {
            ManagedCallEvent last = events.get(events.size() - 1);
            last.endedBy(callEvent);
        }

        events.add(callEvent);
        return callEvent;
    }

    public ManagedCallEvent getLastEvent() {
        return events.get(events.size() - 1);
    }

    public boolean isActive() {
        return this.finished == null;
    }

    public Duration getDuration() {
        final OffsetDateTime end = firstNonNull(finished, OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC));
        return Duration.between(started, end);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ManagedCall that = (ManagedCall) o;
        return Objects.equals(callId, that.callId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(callId);
    }
}
