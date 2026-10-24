package dev.war.openfight.api.event.subscription;

import dev.war.openfight.api.event.CancelableEvent;
import dev.war.openfight.api.event.Event;
import dev.war.openfight.api.event.EventContext;

/// A subscriptionType type that developers can inspect event, cancel history by [EventContext]
/// This event subscriptionType type called even after the event is canceled
@FunctionalInterface
public interface ContextAwareEventSubscription<T extends Event & CancelableEvent> extends EventSubscriptionType<T> {
	void fire(T event, EventContext eventContext);
}
