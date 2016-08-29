package de.qaware.esh.netatmo.internal;

import de.qaware.esh.netatmo.handler.NetatmoHandler;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;

import java.util.Collections;
import java.util.Set;

import static de.qaware.esh.netatmo.NetatmoBindingConstants.THING_TYPE_WEATHER_STATION;

/**
 * The {@link NetatmoHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Moritz Kammerer - Initial contribution
 */
public class NetatmoHandlerFactory extends BaseThingHandlerFactory {

    private final static Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections
            .singleton(THING_TYPE_WEATHER_STATION);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(THING_TYPE_WEATHER_STATION)) {
            return new NetatmoHandler(thing);
        }

        return null;
    }
}
