package de.uhd.campuscoffee.application.service;

import de.uhd.campuscoffee.application.exception.OsmImportException;
import de.uhd.campuscoffee.data.repository.PointOfSaleRepository;
import de.uhd.campuscoffee.domain.model.PointOfSale;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@Service
public class OsmImportService {

    private final RestTemplate restTemplate;
    private final PointOfSaleRepository repository;

    public OsmImportService(RestTemplate restTemplate, PointOfSaleRepository repository) {
        this.restTemplate = restTemplate;
        this.repository = repository;
    }

    public PointOfSale importFromNodeId(long nodeId) {
        String url = "https://www.openstreetmap.org/api/0.6/node/" + nodeId;
        String xml;
        try {
            xml = restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            throw new OsmImportException("Fehler beim Abrufen der OSM-Daten: " + e.getMessage(), e);
        }

        if (xml == null || xml.isBlank()) {
            throw new OsmImportException("Leere Antwort von OSM für Node " + nodeId);
        }

        try {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new org.xml.sax.InputSource(new StringReader(xml)));
            NodeList nodeElements = doc.getElementsByTagName("node");
            if (nodeElements.getLength() == 0) {
                throw new OsmImportException("Keine Node-Elemente in OSM-Antwort für Node " + nodeId);
            }
            Element nodeEl = (Element) nodeElements.item(0);
            String lat = nodeEl.getAttribute("lat");
            String lon = nodeEl.getAttribute("lon");

            PointOfSale pos = new PointOfSale();
            if (lat != null && !lat.isBlank()) pos.setLatitude(Double.parseDouble(lat));
            if (lon != null && !lon.isBlank()) pos.setLongitude(Double.parseDouble(lon));

            NodeList tags = doc.getElementsByTagName("tag");
            for (int i = 0; i < tags.getLength(); i++) {
                Element tag = (Element) tags.item(i);
                String k = tag.getAttribute("k");
                String v = tag.getAttribute("v");
                if ("name".equals(k)) pos.setName(v);
                else if ("addr:street".equals(k)) pos.setStreet(v);
                else if ("addr:city".equals(k)) pos.setCity(v);
            }

            if ((pos.getName() == null || pos.getName().isBlank())
                    && (pos.getStreet() == null || pos.getStreet().isBlank())) {
                throw new OsmImportException("OSM-Node enthält keine verwertbaren POS-Daten (name/addr:street)");
            }

            return repository.save(pos);
        } catch (OsmImportException e) {
            throw e;
        } catch (Exception e) {
            throw new OsmImportException("Fehler beim Parsen der OSM-XML: " + e.getMessage(), e);
        }
    }
}

