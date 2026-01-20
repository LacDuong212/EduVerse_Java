package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.common.exception.AppException;
import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import com.eduverse.eduversebe.common.globalEnums.UserRole;
import com.eduverse.eduversebe.model.Instructor;
import com.eduverse.eduversebe.model.User;
import com.eduverse.eduversebe.repository.InstructorRepository;
import com.eduverse.eduversebe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final UserRepository userRepository;

    @Transactional
    public Instructor createInstructor(User currentUser) {
        if (instructorRepository.existsByUserId(currentUser.getId())) {
            throw new AppException(ErrorCodes.INSTRUCTOR_ALREADY_EXISTS);
        }

        Instructor newInstructor = Instructor.builder()
                .userId(currentUser.getId())
                .isApproved(false)
                .build();

        instructorRepository.save(newInstructor);

        if (currentUser.getRole() == UserRole.student) {
            currentUser.setRole(UserRole.instructor);
            userRepository.save(currentUser);
        }

        return newInstructor;
    }

    public Instructor getCurrentInstructor(String userId) {
        return instructorRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCodes.INSTRUCTOR_NOT_FOUND));
    }
}
