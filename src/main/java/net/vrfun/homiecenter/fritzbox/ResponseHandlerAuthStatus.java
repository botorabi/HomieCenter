/*
 * Copyright (c) 2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import org.w3c.dom.Node;

import javax.validation.constraints.NotNull;
import java.util.HashMap;

/**
 * Response handler for FRITZ!Box authentication information.
 *
 * @author          boto
 * Creation Date    7th June 2018
 */
public class ResponseHandlerAuthStatus extends ResponseHandler<AuthStatus> {

    public ResponseHandlerAuthStatus() {
        setUseCaseSensitiveNames(false);
    }

    @Override
    void setupModel(@NotNull final Node node, @NotNull AuthStatus authStatus) {
        authStatus.setSID(getNodeValue(node, "SID", "0"));
        authStatus.setChallenge(getNodeValue(node, "Challenge", ""));
        authStatus.setBlockTime(convertToInteger(getNodeValue(node, "BlockTime", "0")));

        Node rightsNode = findNode(node, "Rights");
        if (rightsNode != null) {
            HashMap<String, String> rights = new HashMap<>();
            Node nextNode = rightsNode.getFirstChild();
            while(nextNode != null) {
                nextNode = findNextSiblingNode(nextNode, "Name");
                if (nextNode != null) {
                    String name = nextNode.getFirstChild().getNodeValue();
                    nextNode = findNextSiblingNode(nextNode, "Access");
                    if (nextNode != null) {
                        String val = nextNode.getFirstChild().getNodeValue();
                        rights.put(name, val);
                    }
                }
            }
            authStatus.setRights(rights);
        }
    }
}
