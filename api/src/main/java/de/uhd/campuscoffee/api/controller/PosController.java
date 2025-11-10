package de.uhd.campuscoffee.api.controller;

import de.uhd.campuscoffee.application.exception.OsmImportException;
import de.uhd.campuscoffee.application.service.OsmImportService;
import de.uhd.campuscoffee.domain.model.PointOfSale;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pos")
public class PosController {

    // ...existing code...

    private final OsmImportService osmImportService;

    public PosController(/* ...existing injections... */ OsmImportService osmImportService) {
        // ...existing constructor...
        this.osmImportService = osmImportService;
    }

    @PostMapping("/import")
    public ResponseEntity<?> importFromOsm(@RequestParam("nodeId") long nodeId) {
        try {
            PointOfSale saved = osmImportService.importFromNodeId(nodeId);
            return ResponseEntity.ok(saved);
        } catch (OsmImportException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Interner Fehler beim Import");
        }
    }

    // ...existing code...
}

