package com.ximasoftware.persistence;

import com.ximasoftware.EventType;
import com.ximasoftware.event.CallEvent;
import com.ximasoftware.model.ManagedCall;
import com.ximasoftware.model.ManagedCallEvent;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static com.ximasoftware.util.Assertion.checkArgument;
import static com.ximasoftware.util.Assertion.checkState;
import static com.ximasoftware.util.Assertion.isNotEmpty;

/**
 * facade for the level of consistent access to all call data. thread safe.
 */
public class CallEventPersistence {
    private final Map<String, ManagedCall> callsById = new ConcurrentHashMap<>();
    private final Set<ManagedCall> activeCalls = new ConcurrentSkipListSet<>(Comparator.comparing(ManagedCall::getCallId));
    private final IndexedSet<String, ManagedCall> callsByParty = new IndexedSet<>();
    private final IndexedSet<EventType, ManagedCallEvent> callEventsByEventType = new IndexedSet<>();

    public ManagedCall createCall(CallEvent event) {
        checkArgument(event != null, "event can't be null");
        checkArgument(event.getEventType() == EventType.DIAL, "only DIAL events create calls");

        ManagedCall call = callsById.get(event.getCallId());
        if (call == null) {
            synchronized (callsById) {
                call = callsById.get(event.getCallId());
                if (call == null) {
                    call = new ManagedCall(event);
                    callsById.put(event.getCallId(), call);
                }
            }
        }

        activeCalls.add(call);
        callsByParty.add(call.getCallingParty(), call);
        callsByParty.add(call.getReceivingParty(), call);
        return call;
    }

    public void handleCallEvent(CallEvent event) {
        final String id = event.getCallId();

        final ManagedCallEvent last;
        ManagedCall call = callsById.get(id);
        if (call == null) {
            checkState(event.getEventType() == EventType.DIAL, "Cannot handle non-DIAL call events until a call has been DIALed");
            call = createCall(event);
            last = call.getLastEvent();
        } else {
            last = call.somethingHappened(event);
        }

        callEventsByEventType.add(last.getEvent().getEventType(), last);

        if (!call.isActive()) {
            activeCalls.remove(call);
        }
    }

    public int getNumberOfActiveCalls() {
        return activeCalls.size();
    }

    public int getNumberOfCompletedCalls() {
        return callsById.size() - activeCalls.size();
    }

    /**
     * returned in MS
     */
    public long getTotalEventDuration(EventType type) {
        final Set<ManagedCallEvent> events = callEventsByEventType.get(type);
        if (events == null) {
            return 0;
        }

        long millis = events.stream()
                .mapToLong(c -> c.getDuration().toMillis())
                .sum();

        return millis;
    }

    /**
     * returned in MS
     */
    public long getTotalCallTimeForParty(String party) {
        checkArgument(isNotEmpty(party), "party can't be empty");
        final Set<ManagedCall> calls = callsByParty.get(party.trim());

        if (calls == null) {
            return 0;
        }

        long millis = calls.stream()
                .mapToLong(c -> c.getDuration().toMillis())
                .sum();

        return millis;
    }
}
