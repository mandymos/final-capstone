package com.mandy.capstone.controllers;

import com.mandy.capstone.dtos.UserDto;
import com.mandy.capstone.entities.User;
import com.mandy.capstone.repositories.UserRepository;
import com.mandy.capstone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("staff")
public class StaffController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/createuser/{role}")
    @ResponseBody
    //@Responsebody:  annotation can be put on a method and indicates that the return type should be written straight to the HTTP response body (and not placed in a Model, or interpreted as a view name).
    //need it here because I used @Controller vs @RestController
    public List<String> addAcount(@RequestBody UserDto newUser, @PathVariable String role){
        String passHash = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(passHash);
        System.out.println(role);
        return userService.staffAddNewAccount(newUser, role);
    }

    @GetMapping("alluser")
    @ResponseBody
    public List <User> showAllUser(){
        return  userRepository.findAll();
    }

    @GetMapping("/getuser/{userId}")
    @ResponseBody
    public UserDto getUser(@PathVariable Long userId){
        UserDto userDto = userService.getUserByUserId(userId) ;
        userDto.setAuthoritiesDto(null);
        userDto.setPassword(null);
        return userDto;
    }


    @PutMapping("/edituser/{role}")
    @ResponseBody
    public void editUser(@RequestBody UserDto userDto, @PathVariable String role){
        Long userId = userDto.getId();
        UserDto savedUser = new UserDto(userRepository.findById(userId).get());
        //this if is to prevent a staff to create admin role or edit an admin profile
        if(savedUser.getAuthoritiesDto().toString().contains("authority=ROLE_ADMIN") || role.equalsIgnoreCase("ROLE_ADMIN")){
            return;
        }
        //this if statement is needed to avoid user role being switched from borrower to staff/admin and vise versa. if the new role is staffs roles, it may cause db to create a duplicate borrower on borrowers table due to the borrowerdto being null. so if that the case, I will assign the saved borrower on db to the userDto obj.
        if(userDto.getBorrowerDto()!=null){
            userDto.getBorrowerDto().setBorrower_id(savedUser.getBorrowerDto().getBorrower_id());
        }else {
            userDto.setBorrowerDto(savedUser.getBorrowerDto());
        }
////password doesn't show up or pass in request so if password = null, meaning it is unchanged
        if(userDto.getPassword()=="" ||userDto.getPassword()==null){
            userDto.setPassword(savedUser.getPassword());
        }else{
            String passHash = passwordEncoder.encode(userDto.getPassword());
            userDto.setPassword(passHash);
        }
        userService.adminUpdateUserById(userDto,userId, role);

    }

}