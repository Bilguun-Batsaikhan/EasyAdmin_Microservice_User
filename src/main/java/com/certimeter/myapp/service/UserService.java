package com.certimeter.myapp.service;

import com.certimeter.myapp.dto.LoginRequest;
import com.certimeter.myapp.dto.UserDTO;
import com.certimeter.myapp.dto.UserResPagination;
import com.certimeter.myapp.enumeration.MatchMode;
import com.certimeter.myapp.enumeration.ResponseEnum;
import com.certimeter.myapp.enumeration.UserRoleEnum;
import com.certimeter.myapp.exception.FailureException;
import com.certimeter.myapp.repository.jpa.UserRepository;
import com.certimeter.myapp.repository.jpa.UserSpecification;
import com.certimeter.myapp.resourcemodel.User;
import com.certimeter.myapp.enumeration.UserFieldNameUpdateEnum;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.*;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public Map<Long, String> getUsernamesGivenIds(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        Map<Long, String> userIdToUsername = new HashMap<>();
        for (User user : users) {
            userIdToUsername.put(user.getId(), user.getUsername());
        }
        return userIdToUsername;
    }

    // this could be in a common shared library
    private MatchMode getMatchModeFromString(String matchModeStr) {
        switch (matchModeStr.toLowerCase()) {
            case "startswith":
                return MatchMode.STARTS_WITH;
            case "contains":
                return MatchMode.CONTAINS;
            case "notcontains":
                return MatchMode.NOT_CONTAINS;
            case "endswith":
                return MatchMode.ENDS_WITH;
            case "equals":
                return MatchMode.EQUALS;
            case "notequals":
                return MatchMode.NOT_EQUALS;
            case "nofilter":
                return MatchMode.NO_FILTER;
            case "dateis":
                return MatchMode.DATE_IS;
            case "dateisnot":
                return MatchMode.DATE_IS_NOT;
            case "datebefore":
                return MatchMode.DATE_BEFORE;
            case "dateafter":
                return MatchMode.DATE_AFTER;
            default:
                throw new IllegalArgumentException("Invalid match mode: " + matchModeStr);
        }
    }

    public UserResPagination getUsers(
            Optional<String> username, Optional<String> usernameMatchModeStr,
            Optional<String> firstname, Optional<String> firstnameMatchModeStr,
            Optional<String> surname, Optional<String> surnameMatchModeStr,
            Optional<String> phoneNumber, Optional<String> phoneNumberMatchModeStr,
            Optional<String> email, Optional<String> emailMatchModeStr,
            Optional<String> role, Optional<String> roleMatchModeStr, Optional<String> birthdate, Optional<String> birthdateMatchModeStr,
            int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Specification<User> spec = buildUserSpecifications(
                username, usernameMatchModeStr,
                firstname, firstnameMatchModeStr,
                surname, surnameMatchModeStr,
                phoneNumber, phoneNumberMatchModeStr,
                email, emailMatchModeStr,
                role, roleMatchModeStr,
                birthdate, birthdateMatchModeStr
        );

        Page<User> pagedUsers = userRepository.findAll(spec, pageable);
        List<UserDTO> usersDTO = userMapper.usersToUserDtos(pagedUsers.getContent());

        return buildUserResPagination(pagedUsers, usersDTO);
    }

    private Specification<User> buildUserSpecifications(
            Optional<String> username, Optional<String> usernameMatchModeStr,
            Optional<String> firstname, Optional<String> firstnameMatchModeStr,
            Optional<String> surname, Optional<String> surnameMatchModeStr,
            Optional<String> phoneNumber, Optional<String> phoneNumberMatchModeStr,
            Optional<String> email, Optional<String> emailMatchModeStr,
            Optional<String> role, Optional<String> roleMatchModeStr,
            Optional<String> birthdate, Optional<String> birthdateMatchModeStr) {

        Specification<User> spec = Specification.where(null);

        spec = addSpecification(spec, "username", username, usernameMatchModeStr);
        spec = addSpecification(spec, "firstname", firstname, firstnameMatchModeStr);
        spec = addSpecification(spec, "surname", surname, surnameMatchModeStr);
        spec = addSpecification(spec, "phoneNumber", phoneNumber, phoneNumberMatchModeStr);
        spec = addSpecification(spec, "email", email, emailMatchModeStr);
        spec = addSpecification(spec, "role", role, roleMatchModeStr);
        spec = addSpecification(spec, "birthdate", birthdate, birthdateMatchModeStr);
        return spec;
    }

    // This could be in a common shared library
    private Specification<User> addSpecification(
            Specification<User> spec, String field, Optional<String> value, Optional<String> matchModeStr) {

        if (value.isPresent() && matchModeStr.isPresent()) {
            MatchMode matchMode = getMatchModeFromString(matchModeStr.get());
            return spec.and(UserSpecification.matchMode(field, value.get(), matchMode));
        }
        return spec;
    }

    private UserResPagination buildUserResPagination(Page<User> pagedUsers, List<UserDTO> usersDTO) {
        return UserResPagination.builder()
                .pageNo(pagedUsers.getNumber())
                .pageSize(pagedUsers.getSize())
                .totalElements(pagedUsers.getTotalElements())
                .totalPages(pagedUsers.getTotalPages())
                .last(pagedUsers.isLast())
                .data(usersDTO)
                .build();
    }

    public User getUserById(Long id) {
        return userRepository.findUserById(id);
    }

    public boolean addUser(User user) {
        try {
            if (userRepository.existsById(user.getId())) {
                return false;
            }

            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashedPassword);

            userRepository.save(user);
            return true;
        } catch (DataIntegrityViolationException e) {
            // Handle the duplicate entry exception
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new FailureException(ResponseEnum.DUPLICATE_ENTRY);
            }
            throw e;
        }
    }

    public boolean addUsers(List<User> users) {
        for (User user : users) {
            if (userRepository.existsByUsername(user.getUsername())) {
                continue; // Skip this user if the username already exists
            }
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashedPassword);
            userRepository.save(user);
        }
        return true;
    }

    public boolean replaceUser(User user, Long id) {
        return userRepository.findById(id)
                //The map function allows me to specify a block of code to execute only if the Optional contains a value
                .map(existingUser -> {
                    userMapper.updateUserFromDto(user, existingUser);
                    userRepository.save(existingUser);
                    return true;
                }).orElse(false);
    }

    public boolean updateUser(Long id, Map<String, ?> updates) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            updates.forEach((key, value) -> {
                UserFieldNameUpdateEnum fieldEnum = Arrays.stream(UserFieldNameUpdateEnum.values()).filter(enumValue -> enumValue.getFieldname().equals(key)).findFirst().orElse(null);

                if (fieldEnum != null) {
                    switch (fieldEnum) {
                        case USERNAME:
                            user.setUsername((String) value);
                            break;
                        case FIRSTNAME:
                            user.setFirstname((String) value);
                            break;
                        case SURNAME:
                            user.setSurname((String) value);
                            break;
                        case EMAIL:
                            user.setEmail((String) value);
                            break;
                        case PHONE_NUMBER:
                            user.setPhoneNumber((String) value);
                            break;
                        case BIRTHDATE:
                            if (value instanceof String string) {
                                user.setBirthdate(LocalDate.parse(string, formatter));
                            } else if (value instanceof Integer year) {
                                user.setBirthdate(LocalDate.of(year, 1, 1));
                            }
                            break;
                        case ROLE:
                            if (value instanceof String roleStr) {
                                user.setRole(UserRoleEnum.valueOf(roleStr));
                            }
                            break;
                        case PASSWORD:
                            String hashedPassword = BCrypt.hashpw((String) value, BCrypt.gensalt());
                            user.setPassword(hashedPassword);
                            break;
                        default:
                            throw new FailureException(ResponseEnum.INVALID_INPUT);
                    }
                }
            });
            try {
                userRepository.save(user);
                return true;
            } catch (DataIntegrityViolationException e) {
                // Handle the duplicate entry exception
                if (e.getCause() instanceof ConstraintViolationException) {
                    throw new FailureException(ResponseEnum.DUPLICATE_ENTRY);
                }
                throw e;
            }
        }
        return false;
    }


    public boolean removeUser(Long id) {
        try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (DataIntegrityViolationException e) {
            // Handle the foreign key constraint violation
            throw new FailureException(ResponseEnum.FOREIGN_KEY_CONSTRAINT_VIOLATION);
        }
    }

    public User getUserByLoginInfo(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        User user = null;
        if (username != null) {
            user = userRepository.findUserByUsername(username);
        } else if (email != null) {
            user = userRepository.findUserByEmail(email);
        }

        if (user == null) {
            throw new FailureException(ResponseEnum.RESOURCE_NOT_FOUND);
        }

        // Compare the hashed password
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new FailureException(ResponseEnum.AUTHENTICATION_FAILED);
        }

        return user;
    }
}
