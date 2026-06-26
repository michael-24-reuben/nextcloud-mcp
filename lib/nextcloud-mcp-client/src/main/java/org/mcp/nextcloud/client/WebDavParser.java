package org.mcp.nextcloud.client;

import java.io.ByteArrayInputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class WebDavParser {
    List<WebDavResource> resources(byte[] xml) {
        Document document = document(xml);
        NodeList responseNodes = document.getElementsByTagNameNS("*", "response");
        List<WebDavResource> resources = new ArrayList<>();
        for (int index = 0; index < responseNodes.getLength(); index++) {
            Node node = responseNodes.item(index);
            if (node instanceof Element response) {
                resources.add(resource(response));
            }
        }
        return List.copyOf(resources);
    }

    private WebDavResource resource(Element response) {
        String href = firstText(response, "href");
        Element prop = firstElement(response, "prop");
        boolean collection = prop != null && firstElement(prop, "collection") != null;
        Long size = longValue(firstText(prop, "getcontentlength"));
        Integer favorite = intValue(firstText(prop, "favorite"));
        return new WebDavResource(
                href,
                nameFromHref(href),
                collection,
                size,
                firstText(prop, "getcontenttype"),
                firstText(prop, "getetag"),
                firstText(prop, "getlastmodified"),
                favorite,
                firstText(prop, "permissions"),
                firstText(prop, "owner-id"),
                firstText(prop, "owner-display-name"));
    }

    private Document document(byte[] xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            disableExternalEntities(factory);
            return factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml == null ? new byte[0] : xml));
        } catch (Exception ex) {
            throw new NextcloudClientException("invalid_webdav_xml", "Invalid WebDAV multistatus XML", ex);
        }
    }

    private static void disableExternalEntities(DocumentBuilderFactory factory) throws ParserConfigurationException {
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
    }

    private static Element firstElement(Element element, String localName) {
        if (element == null) {
            return null;
        }
        NodeList nodes = element.getElementsByTagNameNS("*", localName);
        if (nodes.getLength() == 0) {
            return null;
        }
        Node first = nodes.item(0);
        return first instanceof Element value ? value : null;
    }

    private static String firstText(Element element, String localName) {
        Element value = firstElement(element, localName);
        if (value == null) {
            return null;
        }
        String text = value.getTextContent();
        return text == null || text.isBlank() ? null : text.trim();
    }

    private static Long longValue(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static Integer intValue(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String nameFromHref(String href) {
        if (href == null || href.isBlank()) {
            return null;
        }
        String trimmed = href.endsWith("/") ? href.substring(0, href.length() - 1) : href;
        int lastSlash = trimmed.lastIndexOf('/');
        String name = lastSlash >= 0 ? trimmed.substring(lastSlash + 1) : trimmed;
        return URLDecoder.decode(name, StandardCharsets.UTF_8);
    }
}
