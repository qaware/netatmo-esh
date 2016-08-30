package de.qaware.esh.netatmo.handler;

import de.qaware.esh.netatmo.NetatmoBindingConstants;
import de.qaware.esh.netatmo.NetatmoWebservice;
import de.qaware.esh.netatmo.model.DashboardData;
import de.qaware.esh.netatmo.model.Device;
import de.qaware.esh.netatmo.model.StationData;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * The {@link NetatmoHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Moritz Kammerer - Initial contribution
 */
public class NetatmoHandler extends BaseThingHandler {

    private Logger logger = LoggerFactory.getLogger(NetatmoHandler.class);

    private static final long DELAY_IN_MINUTES = 10;
    private ScheduledFuture<?> scheduledFuture;

    public NetatmoHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    @Override
    public void initialize() {
        updateStatus(ThingStatus.ONLINE);

        scheduledFuture = scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                refresh();
            }

        }, 0, DELAY_IN_MINUTES, TimeUnit.MINUTES);
    }

    @Override
    public void dispose() {
        scheduledFuture.cancel(false);

        super.dispose();
    }

    private void refresh() {
        String id = getThing().getProperties().get("id");
        logger.debug("Refreshing data for weatherstation {}", id);

        StationData data;
        try {
            data = NetatmoWebservice.INSTANCE.fetchStationData();
        } catch (IOException e) {
            logger.error("Exception while fetching data from webservice", e);
            updateStatus(ThingStatus.OFFLINE);
            return;
        }

        for (Device device : data.getBody().getDevices()) {
            if (device.getId().equals(id)) {
                DashboardData dashboard = device.getDashboardData();

                updateState(NetatmoBindingConstants.CHANNEL_TEMPERATURE, new DecimalType(dashboard.getTemperature()));
                updateState(NetatmoBindingConstants.CHANNEL_HUMIDITY, new DecimalType(dashboard.getHumidity()));
                updateState(NetatmoBindingConstants.CHANNEL_CO2, new DecimalType(dashboard.getCo2()));
                updateStatus(ThingStatus.ONLINE);
                return;
            }
        }

        // Weather station not found, switch to offline
        updateStatus(ThingStatus.OFFLINE);
    }
}
