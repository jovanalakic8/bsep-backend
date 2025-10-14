package com.project.bsep.service;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.project.bsep.dto.*;
import com.project.bsep.dto.LoginDto;
import com.project.bsep.dto.TokenDto;
import com.project.bsep.model.*;
import com.project.bsep.repository.*;
import com.project.bsep.repository.ClientRepository;
import com.project.bsep.security.JwtUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService implements IUserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private CommercialRepository commercialRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;
    private static final Logger logger= LoggerFactory.getLogger(UserService.class);
    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationService notificationService;

    public UserService() { }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public TokenDto login(LoginDto loginDto) {

        Optional<User> userOpt = userRepository.findByUsername(loginDto.getUsername());

        if (userOpt.isEmpty()) {
            logger.error("Losi kredencijali pri logovanju!");
            notificationService.sendToAdmins(findAllAdmins(),"Username: "+loginDto.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message: Incorrect credentials!");
        }
        try{

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication.getAuthorities();
            //System.out.println("Authorities: " + authorities);

            if(!userOpt.get().isUsing2FA()) {
                String accessToken = jwtUtils.generateAccessToken(authentication);
                String refreshToken = jwtUtils.generateRefreshToken(authentication);

                TokenDto tokens = new TokenDto();
                tokens.setAccessToken(accessToken);
                tokens.setRefreshToken(refreshToken);
                return tokens;
            }


            logger.info("Uspesno logovanje korisnika "+ loginDto.getUsername()+"!");
            return new TokenDto("", "");
        }
        catch(Exception e)
        {
            logger.info("Neuspesno logovanje korisnika "+ loginDto.getUsername()+", pogresna lozinka!");
            notificationService.sendToAdmins(findAllAdmins(),"Username: "+loginDto.getUsername());
            e.printStackTrace();
            return null;
        }

    }

    public boolean validateRecaptcha(String secretKey, String captchaResponse) {
        final String verificationUrl = "https://www.google.com/recaptcha/api/siteverify";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String bodyContent = "secret=" + secretKey + "&response=" + captchaResponse;
        HttpEntity<String> requestEntity = new HttpEntity<>(bodyContent, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> apiResponse = restTemplate.postForEntity(verificationUrl, requestEntity, String.class);

        if (apiResponse.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(apiResponse.getBody());

                return jsonResponse.path("success").asBoolean(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public TokenDto verifyTotp(String username, String code) {
        User user = userRepository.findByUsername(username).get();
        TokenDto tokens = new TokenDto();
        String accessToken = jwtUtils.generateAccessToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);

        tokens.setAccessToken(accessToken);
        tokens.setRefreshToken(refreshToken);
        return tokens;
    }

    @Override
    public Boolean sendVerificationEmail(PasswordlessDto passwordlessDto) {
        Optional<User> user = userRepository.findByUsername(passwordlessDto.getEmail());
        if (user.isEmpty()) {
            //System.out.println("User not found");
            logger.error("Korisnik " + passwordlessDto.getEmail() + " nije pronadjen, losi kredencijali!");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "message: Incorrect credentials!");
        }

        Optional<Client> client = clientRepository.findById(user.get().getId());
        if (client.isEmpty()) {
            System.out.println("Client not found");
            logger.error("Klijent " + passwordlessDto.getEmail() + " nije pronadjen!");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "message: Client not found!");
        }

        if(client.get().getServicePackage().equals(ServicePackageType.BASIC)){
            logger.error("Klijent " + passwordlessDto.getEmail() + " je u BASIC paketu!");
            return false;
        }

        //sendEmail(username);
        logger.info("Klijentu " + passwordlessDto.getEmail() + " poslat verifikaioni mejl!");
        sendEmail(passwordlessDto.getEmail());
        return true;
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public boolean hasAuthority(String jwtToken, String permissionName)
    {
        String username=jwtUtils.getUserNameFromToken(jwtToken);
        if(findByUsername(username).isPresent())
        {
            User user=findByUsername(username).get();
            if(user.hasPermission(permissionName))
                return true;
        }
        return false;
    }


    public void sendEmail(String username) {
        String verificationLink = "http://localhost:4200/passwordlessLogin/verify/id="
                + jwtUtils.generateVerificationToken(username);
        String verificationMail = generateVerificationEmail(username, verificationLink);
        emailService.sendNotificaitionAsync(username, "Mejl za passwordless login, BSEP", verificationMail);
        //System.out.println("Email poslat valjda...");
        logger.info("Korisniku "+username+" je poslat mejl!");
    }

    private String generateVerificationEmail(String name, String verificationLink) {
        return String.format(
                "<p>Please click the following link to log in:</p>\n" +
                        "<p><a href=" + verificationLink + ">Activation Link</a></p>\n" +
                        "<p>After 10 minutes the link will not be available anymore.</p>\n" +
                        "<p>Best regards,<br/>The BSEP Team</p>"
                , name, verificationLink);
    }

    @Override
    public String validateVerificationToken(String token) {
        try {

            if (jwtUtils.validateJwtToken(token)
                    && !jwtUtils.isTokenExpired(token)) {
                Optional<Client> client = clientRepository.findByUsername(jwtUtils.getUserNameFromToken(token));
                if (client.isEmpty() || !client.get().isEnabled()) {
                    System.out.println("User not found");
                    logger.error("Korisnik nije pronadjen!");
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "message: User not found!");
                }

                if(client.get().getVerificationToken() != null && client.get().getVerificationToken().equals(token)){
                    logger.error("Pokusaj koriscenja verifikacionog linka vise od jednom!");
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message: Verifaction link can be used " +
                            "only once!");
                }
                client.get().setVerificationToken(token);
                userRepository.save(client.get());
                logger.info("Korisnik "+jwtUtils.getUserNameFromToken(token)+" je verifikovan pomocu mejla!");
                return jwtUtils.getUserNameFromToken(token);
            }
            return null;
        }catch (TokenExpiredException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "message: The link has expired!");
        }
    }

    @Override
    public TokenDto passwordlessLogin(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            //System.out.println("User not found");
            logger.error("Korisnik nije pronadjen!");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "message: User not found!");
        }

        String accessToken = jwtUtils.generateAccessToken(user.get());
        String refreshToken = jwtUtils.generateRefreshToken(user.get());

        TokenDto tokens = new TokenDto();
        tokens.setAccessToken(accessToken);
        tokens.setRefreshToken(refreshToken);
        logger.info("Korisnik "+username+" se uspeno ulogovan bez lozinke!");
        return tokens;
    }

    @Override
    public TokenDto refreshToken(String refreshToken) {
        String username = jwtUtils.getUserNameFromToken(refreshToken);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "message: User not found!");
        }

        String newAccessToken = jwtUtils.generateAccessToken(user.get());
        TokenDto tokenDto = new TokenDto();
        tokenDto.setAccessToken(newAccessToken);
        tokenDto.setRefreshToken(refreshToken);

        return tokenDto;
    }
    public ArrayList<User> findInactiveClients() {
        ArrayList<User> inactiveClients = userRepository.findByRolesNameAndEnabledIsFalseAndActivationPendingIsFalse("client");
        ArrayList<User> result = new ArrayList<>();

        for (User user : inactiveClients) {
            if (user.getBlocked() == null || user.getBlocked().isBefore(LocalDateTime.now())) {
                result.add(user);
            }
        }

        return result;
    }
    @Override
    public Boolean sendMailToActivate(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            //System.out.println("User not found");
            logger.error("Korisnik nije pronadjen!");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "message: Incorrect credentials!");
        }
        user.get().setActivationPending(true);
        userRepository.save(user.get());
        sendActivationEmail(username);
        logger.info("Korisniku "+username+" je poslat atkivacioni mejl!");
        return true;
    }

    public void sendActivationEmail(String username) {
        String activationLink = "http://localhost:4200/accountActivation/id="
                + jwtUtils.generateActivationToken(username);
        String verificationMail = generateActivationEmail(username, activationLink);
        emailService.sendNotificaitionAsync(username, "Activation link, BSEP", verificationMail);
    }
    private String generateActivationEmail(String username, String link) {
        return String.format(
                "<p>Please click the following link to activate your account:</p>\n" +
                        "<p><a href=" + link + ">Activation Link</a></p>\n" +
                        "<p>After 24 hours the link will not be available anymore.</p>\n" +
                        "<p>Best regards,<br/>The BSEP Team</p>"
                , username, link);
    }

    @Override
    public Boolean validateActivationToken(String token) {
        if (jwtUtils.validateJwtToken(token)){
            Optional<User> user = userRepository.findByUsername(jwtUtils.getUserNameFromToken(token));
            if (user.isEmpty()) {
                logger.error("Korisnik "+jwtUtils.getUserNameFromToken(token)+" nije pronadjen!");
                //System.out.println("User not found");
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "message: User not found!");
            }
            if (user.get().isEnabled()) {
                logger.error("Korisnik "+jwtUtils.getUserNameFromToken(token)+" je vec aktiviran!");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User account is already activated!");
            }
            user.get().setEnabled(true);
            user.get().setActivationPending(false);
            userRepository.save(user.get());
            logger.info("Korisnik "+jwtUtils.getUserNameFromToken(token)+" je aktiviran!");
            return true;
        }
        return false;
    }

    @Override
    public Boolean blockUser(String username, String reason)
    {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            //System.out.println("User not found");
            logger.error("Korisnik "+username+" nije pronadjen!");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "message: User not found!");
        }
        LocalDateTime now = LocalDateTime.now();
        user.get().setBlocked(now.plusHours(24));
        userRepository.save(user.get());
        String blockMail = generateBlockEmail(username, reason);
        emailService.sendNotificaitionAsync(username, "The registration request has been denied", blockMail);
        logger.info("Korisnik "+username+" je blokran!");
        return true;
    }
    private String generateBlockEmail(String username, String reason) {
        return String.format(
                "<p>We're sorry, but your registration request has been denied for the following reason: " + reason + ". You cannot register for the next 24 hours.</p>\n" +
                        "<p>Best regards,<br/>The BSEP Team</p>"
                , reason);
    }
    @Override
    public User findById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        User user = userOptional.orElseThrow(() -> new NoSuchElementException("User not found"));
        return user;
    }

    public User updateUser(UserProfileDto dto) throws Exception{
        User user = new User(dto);
        if ((user.getId() == null)){
            logger.error("Korisnik "+dto.getUsername()+" nije pronadjen!");
            throw new Exception("ID must not be null for updating entity.");
        }
        user.setEnabled(true);
        User savedUser = userRepository.save(user);
        logger.info("Korisnik "+dto.getUsername()+" je uspesno azuriran!");
        return savedUser;
    }

    public boolean changePassword(ChangePasswordDto password){
        Optional<User> optionalUser = findByUsername(password.username);
        try {
            if(optionalUser.isPresent()) {
                User user = optionalUser.get();
                if (passwordEncoder.matches(password.oldPassword, user.getPassword()) && password.newPassword.equals(password.repeatedPassword)) {
                    user.setPassword(passwordEncoder.encode(password.newPassword));
                    userRepository.save(user);
                    Optional<Employee> optionalEmployee = employeeRepository.findById(user.getId());
                    if(!optionalEmployee.isEmpty()){
                        Employee employee = optionalEmployee.get();
                        employee.setHasChangedPassword(true);
                        employeeRepository.save(employee);
                        logger.info("Korisnik "+password.username+" je uspesno promenio lozinku!");
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Korisnik "+password.username+" nije uspeo da promeni lozinku!");
        }
        return false;
    }

    public ArrayList<User> findByRolesName(String roleName) {
        return userRepository.findByRolesName(roleName);
    }

    public User createUser(UserCreationDto dto) throws Exception {
        User user = new User(dto);
        user.setName(dto.name);
        user.setSurname(dto.surname);
        user.setUsername(dto.username);
        user.setPassword(passwordEncoder.encode(dto.password));
        user.setAddress(dto.address);
        user.setCity(dto.city);
        user.setCountry(dto.country);
        user.setPhone(dto.phone);
        user.setEnabled(true);
        List<Role> roles = new ArrayList<>();
        Optional<Role> optionalRole = roleRepository.findByName(dto.role);

        if (optionalRole.isPresent()) {
            Role role = optionalRole.get();
            roles.add(role);
            user.setRoles(roles);
        }

        if (user.getId() != null) {
            logger.error("Korisnik "+dto.username+" nije moguce da se kreira!");
            throw new Exception("ID must be null for a new entity.");
        }

        User savedUser = userRepository.save(user);
        logger.info("Korisnik "+dto.username+" je uspesno kreiran!");
        return savedUser;
    }

    public Request createRequest(RequestCreationDto dto) throws Exception {
        Request request = new Request();
        Optional<Client> optionalClient = clientRepository.findById(Long.parseLong(dto.getClientId()));
        if(optionalClient.isPresent()){
            Client client = optionalClient.get();
            request.setClient(client);
        }
        request.setActiveFrom(dto.getActiveFrom());
        request.setActiveTo(dto.getActiveTo());
        request.setDeadline(dto.getDeadline());
        request.setDescription(dto.getDescription());

        if (request.getId() != null) {
            logger.error("Klijent "+optionalClient.get().getUsername()+" nije uspeo da posalje zahtev!");
            throw new Exception("ID must be null for a new entity.");
        }

        Request savedRequest = requestRepository.save(request);
        logger.info("Klijent "+optionalClient.get().getUsername()+" je poslao zahtev!");
        return savedRequest;
    }

    @Override
    public ArrayList<Request> findAllRequestsNotInPast() throws ParseException {
        Date currentDate = new Date();
        List<Request> allRequests = requestRepository.findAll();
        ArrayList<Request> requestsNotInPast = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        for (Request request : allRequests) {
            Date date = formatter.parse(request.getDeadline());
            if (!date.before(currentDate)) {
                requestsNotInPast.add(request);
            }
        }

        return requestsNotInPast;
    }
    public ArrayList<Commercial> findAllCommercials(){
        return  commercialRepository.findAll();
    }

    public Commercial createCommercial(CommercialDto dto) throws Exception {
        Commercial commercial = new Commercial();

        ClientDto clientDto = dto.getClient();
        Client client = new Client();
        Optional<Client> populatedClient = clientRepository.findByUsername(clientDto.getUsername());
        if(populatedClient.isPresent()){
            client = populatedClient.get();
            commercial.setClient(client);
        }

        EmployeeDto employeeDto = dto.getEmployee();
        Employee employee = new Employee();
        Optional<Employee> populatedEmployee = employeeRepository.findById(employeeDto.getId());
        if(populatedEmployee.isPresent()){
            employee = populatedEmployee.get();
            commercial.setEmployee(employee);
        }

        commercial.setDuration(dto.getDuration());
        commercial.setMoto(dto.getMoto());
        commercial.setDescription(dto.getDescription());

        if (commercial.getId() != null) {
            logger.error("Korisnku "+employeeDto.getUsername()+" nije kreirana reklama!");
            throw new Exception("ID must be null for a new entity.");
        }

        Commercial savedCommercial = commercialRepository.save(commercial);
        logger.info("Korisnku "+employeeDto.getUsername()+" je kreirana reklama!");
        return savedCommercial;
    }

    public void deleteRequest(Long id){
        requestRepository.deleteById(id);
    }

    @Override
    public Employee findEmployeeById(Long id) {
        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        Employee employee = employeeOptional.orElseThrow(() -> new NoSuchElementException("Employee not found"));
        return employee;
    }

    public List<User> findAllAdmins()
    {
        List<User> admins=userRepository.findByRolesName("admin");
        return admins;
    }
}