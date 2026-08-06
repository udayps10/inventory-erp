package com.project.inventoryerp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	private BusinessRepository businessRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public String register(@RequestBody AuthRequest request) {
        Business business = new Business();
        business.setName(request.getBusinessName());
        Business savedBusiness = businessRepository.save(business);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("SHOPKEEPER");
        user.setBusiness(savedBusiness);
        userRepository.save(user);
        user.setActive(true); 
        emailService.sendRegistrationEmail(request.getEmail(), request.getUsername());

        return "User registered successfully";
    }@PostMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        return jwtUtil.generateToken(user.getUsername());
    }
    
}