package com.foferys_journal.fofejournal.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foferys_journal.fofejournal.config.CustomOAuth2User;
import com.foferys_journal.fofejournal.models.Fusa;
import com.foferys_journal.fofejournal.models.FusaApiRequest;
import com.foferys_journal.fofejournal.models.FusaApiResponse;
import com.foferys_journal.fofejournal.services.FusaRepository;
import com.foferys_journal.fofejournal.services.FusaService;
import com.foferys_journal.fofejournal.services.UserService;

import jakarta.validation.Valid;

/**
 * API REST per la risorsa Fusa.
 * Base path: /api/fusa
 * Richiede autenticazione (stessa sessione dell'app web).
 */
@RestController
@RequestMapping("/api/fusa")
public class FusaRestController {

    @Autowired
    private FusaRepository fusaRepository;
    @Autowired
    private FusaService fusaService;
    @Autowired
    private UserService userService;

    private String getUsername(Object principal) {
        if (principal instanceof CustomOAuth2User) {
            return ((CustomOAuth2User) principal).getUser().getUsername();
        }
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }

    private int getUserId(Object principal) {
        if (principal instanceof CustomOAuth2User) {
            return ((CustomOAuth2User) principal).getUser().getId();
        }
        if (principal instanceof UserDetails) {
            return userService.getUserByUsername(((UserDetails) principal).getUsername()).getId();
        }
        return -1;
    }

    /** GET /api/fusa o /api/fusa/ – elenco delle Fusa dell'utente autenticato */
    @GetMapping({"", "/"})
    public ResponseEntity<List<FusaApiResponse>> list(@AuthenticationPrincipal Object principal) {
        String username = getUsername(principal);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        int userId = getUserId(principal);
        List<FusaApiResponse> list = fusaRepository.findByUserId(userId).stream()
            .map(FusaApiResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    /** GET /api/fusa/{id} – singola Fusa (solo se di proprietà) */
    @GetMapping("/{id}")
    public ResponseEntity<FusaApiResponse> getOne(@PathVariable int id, @AuthenticationPrincipal Object principal) {
        String username = getUsername(principal);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return fusaRepository.findById(id)
            .filter(f -> f.getUser().getId() == getUserId(principal))
            .map(FusaApiResponse::from)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /** POST /api/fusa – crea una nuova Fusa (body JSON: titolo, contenuto) */
    @PostMapping
    public ResponseEntity<FusaApiResponse> create(
            @Valid @RequestBody FusaApiRequest request,
            @AuthenticationPrincipal Object principal) {
        String username = getUsername(principal);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Fusa saved = fusaService.saveFusaFromApi(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(FusaApiResponse.from(saved));
    }

    /** PUT /api/fusa/{id} – aggiorna titolo e contenuto (solo se di proprietà) */
    @PutMapping("/{id}")
    public ResponseEntity<FusaApiResponse> update(
            @PathVariable int id,
            @Valid @RequestBody FusaApiRequest request,
            @AuthenticationPrincipal Object principal) {
        String username = getUsername(principal);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        int userId = getUserId(principal);
        return fusaRepository.findById(id)
            .filter(f -> f.getUser().getId() == userId)
            .map(fusa -> {
                fusa.setTitolo(request.getTitolo());
                fusa.setContenuto(request.getContenuto());
                fusa.setDataModifica(java.time.LocalDate.now());
                return fusaRepository.save(fusa);
            })
            .map(FusaApiResponse::from)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /** DELETE /api/fusa/{id} – elimina una Fusa (solo se di proprietà) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id, @AuthenticationPrincipal Object principal) {
        String username = getUsername(principal);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        int userId = getUserId(principal);
        Optional<Fusa> opt = fusaRepository.findById(id);
        if (opt.isEmpty() || opt.get().getUser().getId() != userId) {
            return ResponseEntity.notFound().build();
        }
        fusaService.delete(opt.get(), userId);
        return ResponseEntity.noContent().build();
    }
}
