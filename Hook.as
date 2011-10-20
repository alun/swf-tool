package {

import flash.display.Stage;
import flash.events.IEventDispatcher;

public class Hook {
	static public var stage : IEventDispatcher;

	public static function setupEventListener(dispatcher : *, ... params) : void {
		var traceVals : Array = params.slice();
		traceVals.splice(0, 0, dispatcher);
		trace("Hook is setting up the listener for: " + traceVals.join(" ~ "));

		if (dispatcher is Stage) {
			trace("dispatcher is Stage, trying to use internal stage instead...");
			dispatcher = stage;
		}
		if (dispatcher == null) {
			trace("dispatcher is null ignoring...");
		} else {
			dispatcher.addEventListener.apply(dispatcher, params);
			trace("listener is set up");
		}
	}

}
}
