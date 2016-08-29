package de.qaware.esh.netatmo;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link NetatmoBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Moritz Kammerer - Initial contribution
 */
public class NetatmoBindingConstants {

    public static final String BINDING_ID = "netatmo";

    // List of all Thing Type UIDs
    public final static ThingTypeUID THING_TYPE_WEATHER_STATION = new ThingTypeUID(BINDING_ID, "weather-station");

    // List of all Channel ids
    public final static String CHANNEL_1 = "channel1";

}
