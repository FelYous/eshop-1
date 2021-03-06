package nano.project.eshop.controllers;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import nano.project.eshop.models.User;
import nano.project.eshop.services.EmailService;
import nano.project.eshop.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@Controller
public class RegisterController {

    private BCryptPasswordEncoder bCryptPasswordEncoder;
	private UserService userService;
	private EmailService emailService;
 
    @Autowired
    public RegisterController(BCryptPasswordEncoder bCryptPasswordEncoder, UserService userService, EmailService emailService) {
      
      this.bCryptPasswordEncoder = bCryptPasswordEncoder;
      this.userService = userService;
      this.emailService = emailService;
    }


	// Return registration form template
	@RequestMapping(value="/register", method = RequestMethod.GET)
	public ModelAndView showRegistrationPage(ModelAndView modelAndView, User user){
		modelAndView.addObject("user", user);
		modelAndView.setViewName("register");
		return modelAndView;
	}
	
	// Process form input data
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView processRegistrationForm(ModelAndView modelAndView, @Valid User user, BindingResult bindingResult, HttpServletRequest request ,@RequestParam Map requestParams ,RedirectAttributes redir) {
		modelAndView.setViewName("register");

		// Lookup user in database by e-mail
		User userExists = userService.findByEmail(user.getEmail());
		
		System.out.println(userExists);
		
		if (userExists != null) {
			modelAndView.addObject("alreadyRegisteredMessage", "Oops!  There is already a user registered with the email provided.");
			modelAndView.setViewName("register");
			bindingResult.reject("email");
			return modelAndView;
		}
		Zxcvbn passwordCheck = new Zxcvbn();

		Strength strength = passwordCheck.measure((String) requestParams.get("rpassword"));

		if (strength.getScore() < 3) {
			bindingResult.reject("rpassword");

			redir.addFlashAttribute("errorMessage", "Your password is too weak.  Choose a stronger one.");

			return modelAndView;
		}


		// Set new password
		user.setPassword(bCryptPasswordEncoder.encode((CharSequence) requestParams.get("rpassword")));
			
		if (bindingResult.hasErrors()) { 
			modelAndView.setViewName("register");		
		} else { // new user so we create user and send confirmation e-mail
					
			// Disable user until they click on confirmation link in email
		    user.setEnabled(false);
		      
		    // Generate random 36-character string token for confirmation link
		    user.setConfirmationToken(UUID.randomUUID().toString());
		    if(user.getEmail().equals("nanoaticorgix@hotmail.com") || user.getEmail().equals("yousseffalleh@gmail.com") ){
		    	user.setRole("admin");
			}else {
		    	user.setRole("user");
			}
		    userService.saveUser(user);
				
			String appUrl = request.getScheme() + "://" + request.getServerName()+":"+request.getServerPort();
			
			SimpleMailMessage registrationEmail = new SimpleMailMessage();
			registrationEmail.setTo(user.getEmail());
			registrationEmail.setSubject("Registration Confirmation");
			registrationEmail.setText("To confirm your e-mail address, please click the link below:\n"
					+ appUrl + "/confirm?token=" + user.getConfirmationToken());
			registrationEmail.setFrom("noreply@domain.com");
			
			emailService.sendEmail(registrationEmail);
			
			modelAndView.addObject("confirmationMessage", "A confirmation e-mail has been sent to " + user.getEmail());
			modelAndView.setViewName("register");
		}
			
		return modelAndView;
	}
	
	// Process confirmation link
	@RequestMapping(value="/confirm", method = RequestMethod.GET)
	public ModelAndView showConfirmationPage(ModelAndView modelAndView, @RequestParam("token") String token) {
			
		User user = userService.findByConfirmationToken(token);
			
		if (user == null) { // No token found in DB
			modelAndView.addObject("invalidToken", "Oops!  This is an invalid confirmation link.");
		} else { // Token found
			if(user.getEnabled()){
				modelAndView.addObject("errorMessage", "this account is already activated.");

			}
			else {
				modelAndView.addObject("confirmationToken", user.getConfirmationToken());
				user.setEnabled(true);
				modelAndView.addObject("successMessage", "Confirmed account success!");
				// Save user
				userService.saveUser(user);
			}

		}
			
		modelAndView.setViewName("confirm");
		return modelAndView;		
	}
	

}