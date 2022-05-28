package com.ximasoftware.event;

import com.ximasoftware.EventType;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static com.ximasoftware.util.Assertion.checkNotNull;
import static com.ximasoftware.util.Assertion.checkState;
import static com.ximasoftware.util.Assertion.isNotEmpty;

/**
 * A call event is something happening on an in-progress call: a calling party and receiving party are engaging
 * in one of the call events (dial, ring, talk, hold, drop).
 */
public class CallEvent {
    private CallEvent() {
        when = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC);
    }

    private String callId;
    private EventType eventType;
    private String callingParty;
    private String receivingParty;
    private final OffsetDateTime when;

    public String getCallId() {
        return callId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getCallingParty() {
        return callingParty;
    }

    public String getReceivingParty() {
        return receivingParty;
    }

    public OffsetDateTime getWhen() {
        return when;
    }

    static Builder newBuilder() {
        return new Builder();
    }

    static class Builder {
        private final CallEvent event = new CallEvent();

        Builder withCallId(String callId) {
            event.callId = callId;
            return this;
        }

        Builder withEventType(String type) {
            checkState(isNotEmpty(type), "call event type must not be empty");

            final EventType enumType = EventType.valueOf(type.toUpperCase());
            return withEventType(enumType);
        }

        Builder withEventType(EventType type) {
            event.eventType = type;
            return this;
        }

        Builder withCallingParty(String from) {
            event.callingParty = from;
            return this;
        }

        Builder withReceivingParty(String to) {
            event.receivingParty = to;
            return this;
        }

        public CallEvent build() {
            checkNotNull(event.eventType, "event type can't be null");
            checkState(isNotEmpty(event.callId), "call ID can't be null");
            checkState(isNotEmpty(event.callingParty), "from phone can't be null");
            checkState(isNotEmpty(event.receivingParty), "to phone can't be null");

            event.callId = event.callId.trim();
            event.callingParty = event.callingParty.trim();
            event.receivingParty = event.receivingParty.trim();

            return event;
        }
    }
}
