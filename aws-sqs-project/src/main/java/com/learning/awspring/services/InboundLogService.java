package com.learning.awspring.services;

import com.learning.awspring.entities.InboundLog;
import com.learning.awspring.model.response.PagedResult;
import com.learning.awspring.repositories.InboundLogRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class InboundLogService {

    private final InboundLogRepository inboundLogRepository;

    public InboundLogService(InboundLogRepository inboundLogRepository) {
        this.inboundLogRepository = inboundLogRepository;
    }

    public PagedResult<InboundLog> findAllInboundLogs(
            int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort =
                sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                        ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending();

        // create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<InboundLog> inboundLogsPage = inboundLogRepository.findAll(pageable);

        return new PagedResult<>(inboundLogsPage);
    }

    public Optional<InboundLog> findInboundLogById(Long id) {
        return inboundLogRepository.findById(id);
    }

    public InboundLog saveInboundLog(InboundLog inboundLog) {
        return inboundLogRepository.save(inboundLog);
    }

    public void deleteInboundLogById(Long id) {
        inboundLogRepository.deleteById(id);
    }
}
