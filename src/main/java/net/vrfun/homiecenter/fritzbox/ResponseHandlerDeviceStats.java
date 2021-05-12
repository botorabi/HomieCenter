/*
 * Copyright (c) 2018 - 2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import net.vrfun.homiecenter.model.DeviceStats;
import net.vrfun.homiecenter.model.DeviceStatsValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

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
    public void setupModel(@NonNull final Node node, @NonNull DeviceStats deviceStats) {
        Node rootNode = findNode(node, "devicestats");
        if (rootNode != null) {
            NodeList statsNodes = rootNode.getChildNodes();
            for (int i = 0; i < statsNodes.getLength(); ++i) {
                readNextNode(statsNodes.item(i), deviceStats);
            }
        }
    }

    protected void readNextNode(@NonNull final Node statsNode, @NonNull DeviceStats deviceStats) {
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

    protected void readStats(@NonNull final Node statsNode, @NonNull DeviceStatsValues stats) {
        Node node = findFirstChildNode(statsNode, "stats");
        while (node != null) {
            try {
                String grid = getAttributeValue(node, "grid", "0");
                List<Integer> values = createIntegerValues(getNodeValue(node, "stats", ""));
                stats.addStats(Integer.parseInt(grid), values);
            }
            catch(Throwable throwable) {
                LOGGER.debug("Problem occurred while parsing the stats values: {}", throwable.getMessage());
            }
            node = findNextSiblingNode(node, "stats");
        }
    }

    @NonNull
    protected List<Integer> createIntegerValues(@NonNull final String stringValues) {
        List<Integer> values = new ArrayList<>();
        String preparedStringValues = stringValues.replaceAll("-", "-999").trim();
        for (final String value: preparedStringValues.split(",")) {
            values.add(Integer.parseInt(value));
        }
        return values;
    }
}

