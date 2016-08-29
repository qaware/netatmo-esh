package de.qaware.esh.netatmo.discovery;

import de.qaware.esh.netatmo.NetatmoBindingConstants;
import de.qaware.esh.netatmo.NetatmoWebservice;
import de.qaware.esh.netatmo.model.Device;
import de.qaware.esh.netatmo.model.StationData;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class NetatmoDiscoveryService extends AbstractDiscoveryService {
    private static final Set<ThingTypeUID> SUPPORTED_THINGS = new HashSet<>();
    private static final int BACKGROUND_DISCOVERY_TIMEOUT = 10;

    static {
        SUPPORTED_THINGS.add(NetatmoBindingConstants.THING_TYPE_WEATHER_STATION);
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public NetatmoDiscoveryService() throws IllegalArgumentException, IOException {
        super(SUPPORTED_THINGS, BACKGROUND_DISCOVERY_TIMEOUT);

        NetatmoWebservice.INSTANCE.authenticate();
    }

    @Override
    protected void startScan() {
        StationData weatherStations;
        try {
            weatherStations = NetatmoWebservice.INSTANCE.fetchStationData();
        } catch (IOException e) {
            logger.error("Exception while discovering weather stations");
            return;
        }

        for (Device device : weatherStations.getBody().getDevices()) {
            ThingUID uid = new ThingUID(NetatmoBindingConstants.THING_TYPE_WEATHER_STATION, cleanId(device.getId()));

            DiscoveryResult discovery = DiscoveryResultBuilder.create(uid).withLabel(device.getName())
                    .withProperty("id", device.getId()).build();

            thingDiscovered(discovery);
        }
    }

    private String cleanId(String id) {
        return id.replace(':', '-');
    }
}
