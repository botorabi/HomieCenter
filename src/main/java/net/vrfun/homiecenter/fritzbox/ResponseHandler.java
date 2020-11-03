/*
 * Copyright (c) 2018 - 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.fritzbox;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Base class for handling FRITZ!Box responses.
 * It helps to parse the response in XML format and setup a model.
 *
 * @author          boto
 * Creation Date    7th June 2018
 */
public abstract class ResponseHandler<Model> {

    private boolean useCaseSensitiveNames = false;

    /**
     * Use case sensitivity on node or attribute name comparision.
     */
    public void setUseCaseSensitiveNames(boolean useCaseSensitiveNames) {
        this.useCaseSensitiveNames = useCaseSensitiveNames;
    }

    public boolean isUseCaseSensitiveNames() {
        return useCaseSensitiveNames;
    }

    /**
     * Read the XML document and setup the given model, respectively.
     */
    public void read(@NonNull final String response, @NonNull Model model) throws Exception {
        try {
            Document document = parseResponse(response);
            setupModel(document.getFirstChild(), model);
        }
        catch(Throwable throwable) {
            throw new Exception(throwable.getMessage());
        }
    }

    /**
     * Implement the method which gets the document node and fills the model.
     */
    abstract void setupModel(final @NonNull Node node, @NonNull Model model);

    /**
     * Get the value of a node with given name. If it was not found the return the default value.
     */
    protected String getNodeValue(@NonNull final Node node, @NonNull final String fieldName, final String defaultValue) {
        Node fieldNode = findNode(node, fieldName);
        if (fieldNode == null || fieldNode.getFirstChild() == null) {
            return defaultValue;
        }
        return fieldNode.getFirstChild().getNodeValue();
    }

    /**
     * Get the value of a node attribute with given name. Return the default value if it was not found.
     */
    protected String getAttributeValue(@NonNull final Node node, @NonNull final String keyName, final String defaultValue) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes == null) {
            return defaultValue;
        }

        Node attribute = attributes.getNamedItem(keyName);
        if (attribute != null) {
            return attribute.getNodeValue();
        }
        return defaultValue;
    }

    /**
     * Recursively search the DOM tree with the root 'node' and find the next node with given name.
     */
    @Nullable
    protected Node findNode(@NonNull final Node node, @NonNull final String fieldName) {
        if (compareNames(node.getNodeName(), fieldName)) {
            return node;
        }
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node found = findNode(node.getChildNodes().item(i), fieldName);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * Get the next sibling node with given name.
     */
    @Nullable
    protected Node findNextSiblingNode(@NonNull final Node node, @NonNull final String name) {
        for(Node sibling = node.getNextSibling(); sibling != null; sibling = sibling.getNextSibling()) {
            if (compareNames(sibling.getNodeName(), name)) {
                return sibling;
            }
        }
        return null;
    }

    /**
     * Get the first child node with given name.
     */
    @Nullable
    protected Node findFirstChildNode(@NonNull final Node node, @NonNull final String name) {
        NodeList children = node.getChildNodes();
        if (children == null) {
            return null;
        }
        for (int i = 0; i < children.getLength(); i++) {
            if (compareNames(children.item(i).getNodeName(), name)) {
                return children.item(i);
            }
        }
        return null;
    }

    protected boolean convertToBoolean(final String value) {
        return !value.equals("0");
    }

    protected int convertToInteger(String value) {
        try {
            return Integer.parseInt(value);
        }
        catch (Throwable throwable) {}
        return 0;
    }

    @NonNull
    protected Document parseResponse(@NonNull final String response) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new ByteArrayInputStream(response.getBytes()));
    }

    private boolean compareNames(@NonNull final String name1, @NonNull final String name2) {
        if (useCaseSensitiveNames) {
            return name1.equals(name2);
        }
        return name1.equalsIgnoreCase(name2);
    }
}
