/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import net.vrfun.homiecenter.model.*;
import org.springframework.lang.Nullable;
import org.w3c.dom.Node;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Response handler for FRITZ!Box device list.
 *
 * @author          boto
 * Creation Date    7th June 2018
 */
public class ResponseHandlerDeviceList extends ResponseHandler<List<DeviceInfo>> {

    public ResponseHandlerDeviceList() {
        setUseCaseSensitiveNames(false);
    }

    @Override
    public void setupModel(@NotNull final Node node, @NotNull List<DeviceInfo> devices) {
        Node rootNode = findNode(node, "devicelist");
        if (rootNode != null) {
            Node deviceNode = findFirstChildNode(rootNode, "device");
            while(deviceNode != null) {
                deviceNode = readNextNode(devices, deviceNode);
            }
        }
    }

    @Nullable
    protected Node readNextNode(@NotNull final List<DeviceInfo> devices, @NotNull final Node deviceNode) {
        DeviceInfo deviceInfo;
        if (isHeatController(deviceNode)) {
            deviceInfo = new HeatControllerInfo();
            getHeadControllerInfo(deviceNode, (HeatControllerInfo) deviceInfo);
        }
        else {
            deviceInfo = new SwitchDeviceInfo();
            getSwitchInfo(deviceNode, (SwitchDeviceInfo)deviceInfo);
        }
        getDeviceAttributes(deviceNode, deviceInfo);
        getGeneralDeviceInfo(deviceNode, deviceInfo);

        devices.add(deviceInfo);
        return findNextSiblingNode(deviceNode, "device");
    }

    protected void getDeviceAttributes(@NotNull final Node deviceNode, @NotNull final DeviceInfo deviceInfo) {
        deviceInfo.setId(getAttributeValue(deviceNode, "id", ""));
        deviceInfo.setAIN(getAttributeValue(deviceNode, "identifier", ""));
        deviceInfo.setFirmware(getAttributeValue(deviceNode, "fwversion", ""));
        deviceInfo.setProductName(getAttributeValue(deviceNode, "productname", ""));
    }

    protected void getGeneralDeviceInfo(@NotNull final Node deviceNode, @NotNull final DeviceInfo device) {
        device.setName(getNodeValue(deviceNode, "name", "<No Name>"));
        device.setPresent(convertToBoolean(getNodeValue(deviceNode, "present", "0")));
    }

    protected boolean isHeatController(@NotNull final Node deviceNode) {
        return findNode(deviceNode, "hkr") != null;
    }

    protected void getSwitchInfo(@NotNull final Node deviceNode, @NotNull final SwitchDeviceInfo device) {
        device.setOn(convertToBoolean(getNodeValue(deviceNode, "state", "0")));
        device.setVoltage(convertToInteger(getNodeValue(deviceNode, "voltage", "0")));
        device.setPower(convertToInteger(getNodeValue(deviceNode, "power", "0")));
        device.setEnergy(convertToInteger(getNodeValue(deviceNode, "energy", "0")));
        device.setTemperature(convertToInteger(getNodeValue(deviceNode, "celsius", "0")));
        device.setTemperatureOffset(convertToInteger(getNodeValue(deviceNode, "offset", "0")));
    }

    protected void getHeadControllerInfo(@NotNull final Node deviceNode, @NotNull final HeatControllerInfo device) {
        device.setBatteryLow(convertToBoolean(getNodeValue(deviceNode, "batterylow", "0")));
        device.setBatteryLevel(convertToInteger(getNodeValue(deviceNode, "battery", "0")));
        device.setComfortTemperature(convertToInteger(getNodeValue(deviceNode, "komfort", "0")));
        device.setEconomyTemperature(convertToInteger(getNodeValue(deviceNode, "absenk", "0")));
        device.setCurrentTemperature(convertToInteger(getNodeValue(deviceNode, "tist", "0")));
        device.setSetTemperature(convertToInteger(getNodeValue(deviceNode, "tsoll", "0")));
        device.setErrorCode(convertToInteger(getNodeValue(deviceNode, "errorcode", "0")));
        device.setWindowOpen(convertToBoolean(getNodeValue(deviceNode, "windowopenactiv", "0")));
    }
}

