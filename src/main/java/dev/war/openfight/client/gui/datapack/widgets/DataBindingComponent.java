package dev.war.openfight.client.gui.datapack.widgets;

import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;

/// We're refactoring UI codes, use [dev.war.openfight.client.gui.widgets.common.DataBoundWidget] instead
@Deprecated
public interface DataBindingComponent<T, R> extends ResizableComponent {
	public void reset();
	public T _getValue();
	public void _setValue(@Nullable T value);
	public void _setResponder(Consumer<R> responder);
	public Consumer<R> _getResponder();
}