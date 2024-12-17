package com.certimeter.myapp.service;

import com.certimeter.myapp.dto.LoginRequest;
import com.certimeter.myapp.dto.UserDTO;
import com.certimeter.myapp.dto.UserResPagination;
import com.certimeter.myapp.enumeration.MatchMode;
import com.certimeter.myapp.enumeration.ResponseEnum;
import com.certimeter.myapp.enumeration.UserRoleEnum;
import com.certimeter.myapp.exception.FailureException;
import com.certimeter.myapp.repository.jpa.UserRepository;
import com.certimeter.myapp.resourcemodel.User;
import com.certimeter.myapp.enumeration.UserFieldNameUpdateEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
            default:
                throw new IllegalArgumentException("Invalid match mode: " + matchModeStr);
        }
    }

    public UserResPagination getUsers(Optional<String> username, Optional<String> matchModeStr, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> pagedUsers;

        if (username.isPresent() && matchModeStr.isPresent()) {
            String usernameValue = username.get();
            MatchMode matchMode = getMatchModeFromString(matchModeStr.get());
            pagedUsers = filterUsersByMatchMode(usernameValue, matchMode, pageable);
        } else {
            pagedUsers = userRepository.findAll(pageable);
        }

        List<User> users = pagedUsers.getContent();
        List<UserDTO> usersDTO = userMapper.usersToUserDtos(users);

        UserResPagination userResPagination = new UserResPagination();
        userResPagination.setPageNo(pagedUsers.getNumber());
        userResPagination.setPageSize(pagedUsers.getSize());
        userResPagination.setTotalElements(pagedUsers.getTotalElements());
        userResPagination.setTotalPages(pagedUsers.getTotalPages());
        userResPagination.setLast(pagedUsers.isLast());
        userResPagination.setData(usersDTO);

        return userResPagination;
    }


    private Page<User> filterUsersByMatchMode(String username, MatchMode matchMode, Pageable pageable) {
        switch (matchMode) {
            case STARTS_WITH:
                return userRepository.findByUsernameStartingWith(username, pageable);
            case CONTAINS:
                return userRepository.findByUsernameContaining(username, pageable);
            case NOT_CONTAINS:
                return userRepository.findByUsernameNotContaining(username, pageable);
            case ENDS_WITH:
                return userRepository.findByUsernameEndingWith(username, pageable);
            case EQUALS:
                return userRepository.findByUsername(username, pageable);
            case NOT_EQUALS:
                return userRepository.findByUsernameNot(username, pageable);
            case NO_FILTER:
            default:
                return userRepository.findAll(pageable);
        }
    }

    public User getUserById(Long id) {
        return userRepository.findUserById(id);
    }

    public boolean addUser(User user) {
        if (userRepository.existsById(user.getId())) {
            return false;
        }

        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);

        userRepository.save(user);
        return true;
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
                        default:
                            throw new FailureException(ResponseEnum.INVALID_INPUT);
                    }
                }
            });
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean removeUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
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
