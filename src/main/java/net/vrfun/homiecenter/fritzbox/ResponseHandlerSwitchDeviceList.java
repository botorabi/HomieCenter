/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import net.vrfun.homiecenter.model.DeviceInfo;
import org.w3c.dom.Node;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Response handler for FRITZ!Box device list.
 *
 * @author          boto
 * Creation Date    7th June 2018
 */
public class ResponseHandlerSwitchDeviceList extends ResponseHandler<List<DeviceInfo>> {

    public ResponseHandlerSwitchDeviceList() {
        setUseCaseSensitiveNames(false);
    }

    public
    @Override
    void setupModel(@NotNull final Node node, @NotNull List<DeviceInfo> devices) {
        Node rootNode = findNode(node, "devicelist");
        if (rootNode != null) {
            Node deviceNode = findFirstChildNode(rootNode, "device");
            while(deviceNode != null) {
                DeviceInfo device = new DeviceInfo();
                getDeviceAttributes(deviceNode, device);
                getDeviceInfo(deviceNode, device);
                devices.add(device);

                deviceNode = findNextSiblingNode(deviceNode, "device");
            }
        }
    }

    private void getDeviceAttributes(@NotNull final Node deviceNode, @NotNull final DeviceInfo device) {
        device.setId(getAttributeValue(deviceNode, "id", ""));
        device.setAIN(getAttributeValue(deviceNode, "identifier", ""));
        device.setFirmware(getAttributeValue(deviceNode, "fwversion", ""));
        device.setProductName(getAttributeValue(deviceNode, "productname", ""));
    }

    private void getDeviceInfo(@NotNull final Node deviceNode, @NotNull final DeviceInfo device) {
        device.setName(getNodeValue(deviceNode, "name", "<No Name>"));
        device.setPresent(convertToBoolean(getNodeValue(deviceNode, "present", "0")));
        device.setOn(convertToBoolean(getNodeValue(deviceNode, "state", "0")));
        device.setVoltage(convertToInteger(getNodeValue(deviceNode, "voltage", "0")));
        device.setPower(convertToInteger(getNodeValue(deviceNode, "power", "0")));
        device.setEnergy(convertToInteger(getNodeValue(deviceNode, "energy", "0")));
        device.setTemperature(convertToInteger(getNodeValue(deviceNode, "celsius", "0")));
        device.setTemperatureOffset(convertToInteger(getNodeValue(deviceNode, "offset", "0")));
    }
}
