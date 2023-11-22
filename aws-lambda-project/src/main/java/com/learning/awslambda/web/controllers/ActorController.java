package com.learning.awslambda.web.controllers;

import com.learning.awslambda.exception.ActorNotFoundException;
import com.learning.awslambda.model.query.FindActorsQuery;
import com.learning.awslambda.model.request.ActorRequest;
import com.learning.awslambda.model.response.ActorResponse;
import com.learning.awslambda.model.response.PagedResult;
import com.learning.awslambda.services.ActorService;
import com.learning.awslambda.utils.AppConstants;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/actors")
@Slf4j
@RequiredArgsConstructor
public class ActorController {

    private final ActorService actorService;

    @GetMapping
    public PagedResult<ActorResponse> getAllActors(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false)
                    int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false)
                    int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false)
                    String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false)
                    String sortDir) {
        FindActorsQuery findActorsQuery = new FindActorsQuery(pageNo, pageSize, sortBy, sortDir);
        return actorService.findAllActors(findActorsQuery);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActorResponse> getActorById(@PathVariable Long id) {
        return actorService.findActorById(id).map(ResponseEntity::ok).orElseThrow(() -> new ActorNotFoundException(id));
    }

    @PostMapping
    public ResponseEntity<ActorResponse> createActor(@RequestBody @Validated ActorRequest actorRequest) {
        ActorResponse response = actorService.saveActor(actorRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/api/actors/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActorResponse> updateActor(
            @PathVariable Long id, @RequestBody @Valid ActorRequest actorRequest) {
        return ResponseEntity.ok(actorService.updateActor(id, actorRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ActorResponse> deleteActor(@PathVariable Long id) {
        return actorService
                .findActorById(id)
                .map(actor -> {
                    actorService.deleteActorById(id);
                    return ResponseEntity.ok(actor);
                })
                .orElseThrow(() -> new ActorNotFoundException(id));
    }
}
