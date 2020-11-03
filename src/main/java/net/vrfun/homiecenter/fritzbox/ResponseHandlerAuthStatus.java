/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import org.springframework.lang.NonNull;
import org.w3c.dom.Node;

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
    void setupModel(@NonNull final Node node, @NonNull AuthStatus authStatus) {
        authStatus.setSID(getNodeValue(node, "SID", "0"));
        authStatus.setChallenge(getNodeValue(node, "Challenge", ""));
        authStatus.setBlockTime(convertToInteger(getNodeValue(node, "BlockTime", "0")));

        Node rightsNode = findNode(node, "Rights");
        if (rightsNode != null) {
            authStatus.setRights(collectRights(rightsNode));
        }
    }

    private HashMap<String, String> collectRights(@NonNull final Node rightsNode) {
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
        return rights;
    }
}
