package com.setupbuilder.service;

import com.setupbuilder.dto.CommentRequest;
import com.setupbuilder.dto.CommentResponse;
import com.setupbuilder.entity.Build;
import com.setupbuilder.entity.Comment;
import com.setupbuilder.entity.User;
import com.setupbuilder.exception.ResourceNotFoundException;
import com.setupbuilder.repository.BuildRepository;
import com.setupbuilder.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BuildRepository buildRepository;

    @Transactional(readOnly = true)
    public Page<CommentResponse> listForBuild(Long buildId, int page, int size) {
        if (!buildRepository.existsById(buildId)) {
            throw new ResourceNotFoundException("Build not found: " + buildId);
        }
        Pageable pageable = PageRequest.of(page, size);
        return commentRepository
                .findByBuildIdOrderByCreatedAtDesc(buildId, pageable)
                .map(CommentResponse::from);
    }

    @Transactional
    public CommentResponse add(Long buildId, CommentRequest req, User author) {
        Build build = buildRepository.findById(buildId)
                .orElseThrow(() -> new ResourceNotFoundException("Build not found: " + buildId));

        Comment comment = new Comment();
        comment.setContent(req.content().trim());
        comment.setUser(author);
        comment.setBuild(build);

        return CommentResponse.from(commentRepository.save(comment));
    }

    @Transactional
    public void delete(Long commentId, User requester) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + commentId));

        boolean isAuthor = comment.getUser().getId().equals(requester.getId());
        boolean isAdmin = requester.getRole().name().equals("ADMIN");

        if (!isAuthor && !isAdmin) {
            throw new AccessDeniedException("You can only delete your own comments.");
        }
        commentRepository.delete(comment);
    }
}