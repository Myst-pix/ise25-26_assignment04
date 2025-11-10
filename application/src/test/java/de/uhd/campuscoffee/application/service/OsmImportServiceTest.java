package de.uhd.campuscoffee.application.service;

import de.uhd.campuscoffee.data.repository.PointOfSaleRepository;
import de.uhd.campuscoffee.domain.model.PointOfSale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class OsmImportServiceTest {

    private RestTemplate restTemplate;
    private PointOfSaleRepository repository;
    private OsmImportService service;

    @BeforeEach
    void setup() {
        restTemplate = mock(RestTemplate.class);
        repository = mock(PointOfSaleRepository.class);
        service = new OsmImportService(restTemplate, repository);
    }

    @Test
    void importFromNodeId_parsesAndSaves() {
        long nodeId = 5589879349L;
        String xml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <osm version="0.6" generator="CGImap 0.8.8">
                  <node id="5589879349" lat="49.009" lon="8.403">
                    <tag k="amenity" v="cafe"/>
                    <tag k="name" v="Rada Coffee &amp; Rösterei"/>
                    <tag k="addr:street" v="Beispielstraße 1"/>
                    <tag k="addr:city" v="Musterstadt"/>
                  </node>
                </osm>
                """;

        when(restTemplate.getForObject("https://www.openstreetmap.org/api/0.6/node/" + nodeId, String.class))
                .thenReturn(xml);

        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ArgumentCaptor<PointOfSale> cap = ArgumentCaptor.forClass(PointOfSale.class);

        PointOfSale saved = service.importFromNodeId(nodeId);

        verify(repository, times(1)).save(cap.capture());
        PointOfSale captured = cap.getValue();

        assertThat(captured.getName()).isEqualTo("Rada Coffee & Rösterei");
        assertThat(captured.getStreet()).isEqualTo("Beispielstraße 1");
        assertThat(captured.getCity()).isEqualTo("Musterstadt");
        assertThat(captured.getLatitude()).isEqualTo(49.009);
        assertThat(captured.getLongitude()).isEqualTo(8.403);

        assertThat(saved).isNotNull();
    }

    @Test
    void importFromNodeId_throwsOnEmptyResponse() {
        long nodeId = 123L;
        when(restTemplate.getForObject("https://www.openstreetmap.org/api/0.6/node/" + nodeId, String.class))
                .thenReturn("");

        try {
            service.importFromNodeId(nodeId);
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("Leere Antwort");
        }
    }
}

