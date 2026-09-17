package com.setupbuilder.service;

import com.setupbuilder.dto.BuildResponse;
import com.setupbuilder.entity.Build;
import com.setupbuilder.exception.ResourceNotFoundException;
import com.setupbuilder.repository.BuildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CuratedService {

    private final BuildRepository buildRepository;

    /** Public — used by landing page. */
    @Transactional(readOnly = true)
    public List<BuildResponse> listCurated() {
        Pageable p = PageRequest.of(0, 12);
        return buildRepository.findCuratedOrdered(p).stream()
                .map(b -> BuildResponse.from(b, List.of()))
                .toList();
    }

    /** Admin — mark a build as curated. Rank defaults to the end (unranked). */
    @Transactional
    public void add(Long buildId) {
        Build b = buildRepository.findById(buildId)
                .orElseThrow(() -> new ResourceNotFoundException("Build not found: " + buildId));
        b.setCurated(true);
        if (b.getCuratedRank() == null) {
            b.setCuratedRank(nextRank());
        }
        buildRepository.save(b);
    }

    @Transactional
    public void remove(Long buildId) {
        Build b = buildRepository.findById(buildId)
                .orElseThrow(() -> new ResourceNotFoundException("Build not found: " + buildId));
        b.setCurated(false);
        b.setCuratedRank(null);
        buildRepository.save(b);
    }

    @Transactional
    public void setRank(Long buildId, Integer rank) {
        Build b = buildRepository.findById(buildId)
                .orElseThrow(() -> new ResourceNotFoundException("Build not found: " + buildId));
        if (!Boolean.TRUE.equals(b.getCurated())) {
            throw new IllegalArgumentException("Build is not curated.");
        }
        b.setCuratedRank(rank);
        buildRepository.save(b);
    }

    private int nextRank() {
        Integer max = buildRepository.findMaxCuratedRank();
        return (max == null ? 0 : max) + 1;
    }
}