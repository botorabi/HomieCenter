/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import net.vrfun.homiecenter.model.*;
import org.slf4j.*;
import org.springframework.data.util.Pair;
import org.w3c.dom.*;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Response handler for FRITZ!Box device list.
 *
 * @author          boto
 * Creation Date    25th September 2018
 */
public class ResponseHandlerDeviceStats extends ResponseHandler<DeviceStats> {

    private final Logger LOGGER = LoggerFactory.getLogger(ResponseHandlerDeviceStats.class);

    public ResponseHandlerDeviceStats() {
        setUseCaseSensitiveNames(false);
    }

    @Override
    public void setupModel(@NotNull final Node node, @NotNull DeviceStats deviceStats) {
        Node rootNode = findNode(node, "devicestats");
        if (rootNode != null) {
            NodeList statsNodes = rootNode.getChildNodes();
            for (int i = 0; i < statsNodes.getLength(); ++i) {
                readNextNode(statsNodes.item(i), deviceStats);
            }
        }
    }

    protected void readNextNode(@NotNull final Node statsNode, @NotNull DeviceStats deviceStats) {
        switch (statsNode.getNodeName()) {
            case "temperature":
                readStats(statsNode, deviceStats.getTemperature());
                break;

            case "power":
                readStats(statsNode, deviceStats.getPower());
                break;

            case "energy":
                readStats(statsNode, deviceStats.getEnergy());
                break;
        }
    }

    protected void readStats(@NotNull final Node statsNode, @NotNull DeviceStatsValues stats) {
        Node node = findFirstChildNode(statsNode, "stats");
        while (node != null) {
            try {
                String grid = getAttributeValue(node, "grid", "0");
                List<Integer> values = createIntegerValues(getNodeValue(statsNode, "stats", ""));
                stats.addStats(Integer.parseInt(grid), values);
            }
            catch(Throwable throwable) {
                LOGGER.debug("Problem occurred while parsing the stats values: {}", throwable.getMessage());
            }
            node = findNextSiblingNode(node, "stats");
        }
    }

    @NotNull
    protected List<Integer> createIntegerValues(@NotNull final String stringValues) {
        List<Integer> values = new ArrayList<>();
        String preparedStringValues = stringValues.replaceAll("-", "-999");
        for (final String value: preparedStringValues.split(",")) {
            values.add(Integer.parseInt(value));
        }
        return values;
    }
}

