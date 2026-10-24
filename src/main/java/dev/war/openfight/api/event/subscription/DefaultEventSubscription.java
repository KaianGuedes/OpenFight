package dev.war.openfight.api.event.subscription;

import dev.war.openfight.api.event.Event;

/// A default event subscriptionType type which is not cancelable, and interruptible by higher prioritized events
@FunctionalInterface
public interface DefaultEventSubscription<T extends Event> extends EventSubscriptionType<T> {
	void fire(T event);
}
