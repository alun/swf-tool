package {

import flash.display.Stage;
import flash.events.IEventDispatcher;

/**
 * This class is used to hook into Google Map addEventListener calls.
 * Byte code replacement technique is used to convert all the addEventListener calls in map_1_20.swc
 * with the call of proxy method GoogleMapsHook.setupEventListener
 * So map than can be used in pre-rolls where stage is not guaranteed to have the access rights.
 *
 * See ticket http://youtrack.oggifinogi.com/issue/FP-787 for details.
 */
public class Hook {
	static public var stage : IEventDispatcher;

	/**
	 * Proxy method which is called in modified version of the map_1_20.swc
	 * @param dispatcher this may be not the IEventDispatcher object so has type of *
	 */
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
