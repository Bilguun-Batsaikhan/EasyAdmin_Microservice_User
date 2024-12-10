package com.certimetergroup.myapp.service;

import com.certimetergroup.myapp.dto.LoginRequest;
import com.certimetergroup.myapp.dto.UserDTO;
import com.certimetergroup.myapp.dto.UserResPagination;
import com.certimetergroup.myapp.enumeration.ResponseEnum;
import com.certimetergroup.myapp.enumeration.UserRoleEnum;
import com.certimetergroup.myapp.exception.FailureException;
import com.certimetergroup.myapp.repository.jpa.UserRepository;
import com.certimetergroup.myapp.resourcemodel.User;
import com.certimetergroup.myapp.enumeration.UserFieldNameUpdateEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.*;

//TODO: Perhaps it's better to service methods throw an exception instead of returning boolean

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResPagination getUsers(Optional<Integer> age, int pageNo, int pageSize) {
        Pageable pagebale = PageRequest.of(pageNo, pageSize);
        Page<User> pagedUsers = userRepository.findAll(pagebale);
        List<User> users = pagedUsers.getContent();
        List<UserDTO> usersDTO = userMapper.usersToUserDtos(users);

        UserResPagination userResPagination = new UserResPagination();

        userResPagination.setPageNo(pagedUsers.getNumber());
        userResPagination.setPageSize(pagedUsers.getSize());
        userResPagination.setTotalElements(pagedUsers.getTotalElements());
        userResPagination.setTotalPages(pagedUsers.getTotalPages());
        userResPagination.setLast(pagedUsers.isLast());

        // https://www.omnicalculator.com/everyday-life/age-in-years#how-to-calculate-age
        if (age.isPresent()) {
            List<UserDTO> usersFilteredByAge = new ArrayList<>();
            LocalDate currentDate = LocalDate.now();

            int currentYear = currentDate.getYear();
            int currentDay = currentDate.getDayOfMonth();
            int currentMonth = currentDate.getMonthValue();
            int ageToCompare;

            for (UserDTO user : usersDTO) {
                int yearDifference = currentYear - user.getBirthdate().getYear();
                int monthDifference = currentMonth - user.getBirthdate().getMonthValue();
                int dayDifference = currentDay - user.getBirthdate().getDayOfMonth();
                int daysAlive = (yearDifference * 365) + (monthDifference * 31) + dayDifference;
                ageToCompare = daysAlive / 365;
                if (ageToCompare == age.get()) {
                    usersFilteredByAge.add(user);
                }
            }
            userResPagination.setData(usersFilteredByAge);
            return userResPagination;
        }
        userResPagination.setData(usersDTO);
        return userResPagination;
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
