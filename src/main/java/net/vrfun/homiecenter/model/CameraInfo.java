/*
 * Copyright (c) 2017-2018 by Botorabi. All rights reserved.
 * https://github.com/botorabi/HomieCenter
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.homiecenter.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * An IP camera is represented by this class.
 *
 * @author          boto
 * Creation Date    11th June 2018
 */
@Entity
public class CameraInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String previewUrl;
    @Transient
    private String previewUrlTag;
    private String url;
    @Transient
    private String urlTag;

    public CameraInfo() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getPreviewUrlTag() {
        return previewUrlTag;
    }

    public void setPreviewUrlTag(String previewUrlTag) {
        this.previewUrlTag = previewUrlTag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlTag() {
        return urlTag;
    }

    public void setUrlTag(String urlTag) {
        this.urlTag = urlTag;
    }

    @Override
    public String toString() {
        return  "name: " + name + ", " +
                "preview URL: " + previewUrl + ", " +
                "URL: " + url;
    }
}
