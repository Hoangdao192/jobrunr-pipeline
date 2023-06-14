package com.viettel.labelling.controller;

import com.viettel.labelling.dto.PipelineDto;
import com.viettel.labelling.service.PipelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("pipeline")
public class PipelineController {

    @Autowired
    private PipelineService pipelineService;

    @PostMapping
    public ResponseEntity<PipelineDto> createPipeline(@RequestBody PipelineDto pipelineDto) {
        System.out.println(pipelineDto);
        return ResponseEntity.ok(pipelineService.createPipeline(pipelineDto));
    }

    @PutMapping
    public ResponseEntity<PipelineDto> updatePipeline(@RequestBody PipelineDto pipelineDto) {
        return ResponseEntity.ok(pipelineService.updatePipeLine(pipelineDto));
    }

    @GetMapping("{id}")
    public ResponseEntity<PipelineDto> getPipeline(@PathVariable String id) {
        return ResponseEntity.ok(pipelineService.getPipeline(id));
    }

    @GetMapping
    public ResponseEntity<Page<PipelineDto>> getAllPipeline(
            @RequestParam String state,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize
    ) {
        return ResponseEntity.ok(pipelineService.getAllPipeline(
                state, page, pageSize
        ));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deletePipeline(@PathVariable String id) {
        return ResponseEntity.ok(pipelineService.deletePipeLine(id));
    }

    @PostMapping("{id}/stop")
    public ResponseEntity<String> stopPipeline(@PathVariable String id) {
        return ResponseEntity.ok(pipelineService.stopPipeline(id));
    }

    @PostMapping("{id}/start")
    public ResponseEntity<String> startPipeline(@PathVariable String id) {
        return ResponseEntity.ok(pipelineService.startPipeline(id));
    }

    @PostMapping("{id}/reschedule")
    public ResponseEntity<PipelineDto> reschedulePipeline(
            @PathVariable String id, @RequestParam LocalDateTime localDateTime) {
        return ResponseEntity.ok(pipelineService.reschedule(id, localDateTime));
    }

    @PostMapping("{id}/pause")
    public ResponseEntity<String> pausePipeline(@PathVariable String id) {
        return ResponseEntity.ok(pipelineService.pausePipeline(id));
    }

    @PostMapping("{id}/resume")
    public ResponseEntity<String> resumePipeline(@PathVariable String id) {
        return ResponseEntity.ok(pipelineService.resumePipeline(id));
    }

}
