package com.ximasoftware.impl;

import com.ximasoftware.CallDataHandler;
import com.ximasoftware.EventType;
import com.ximasoftware.event.CallEvent;
import com.ximasoftware.event.CallEventFactory;
import com.ximasoftware.event.InvalidCallEventException;
import com.ximasoftware.persistence.CallEventPersistence;

/**
 * The call event persistence could and arguably should live here in this class, but because of the multiplexing
 * involved, and parsing/handling raw event data doesn't require any of that, we don't do it here. we do it in its
 * own thread safe class which this delegates to.
 */
public class CallDataHandlerImpl implements CallDataHandler {
	private final CallEventFactory callEventFactory = new CallEventFactory();
	private final CallEventPersistence callEventPersistence = new CallEventPersistence();

	@Override
	public void onCallData(String data) {
		final CallEvent event;
		try {
			event = callEventFactory.createEventFromRawFeed(data);
		} catch (InvalidCallEventException e) {
			// ideally, the interface would define handling the checked exception
			throw new RuntimeException(e);
		}

		callEventPersistence.handleCallEvent(event);
	}

	@Override
	public int getNumberOfActiveCalls() {
		return callEventPersistence.getNumberOfActiveCalls();
	}

	@Override
	public int getNumberOfCompletedCalls() {
		return callEventPersistence.getNumberOfCompletedCalls();
	}

	@Override
	public long getTotalEventDuration(EventType type) {
		return callEventPersistence.getTotalEventDuration(type);
	}

	@Override
	public long getTotalCallTimeForParty(String party) {
		return callEventPersistence.getTotalCallTimeForParty(party);
	}

}
