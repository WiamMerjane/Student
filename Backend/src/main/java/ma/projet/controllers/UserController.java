package ma.projet.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ma.projet.entities.User;
import ma.projet.repository.UserRepository;
import ma.projet.services.UserService;

@RestController
@RequestMapping("/api/v1/users")
//@RequiredArgsConstructor
public class UserController {
	
	@Autowired
	private UserService UserService;
	

	@GetMapping
	public List<User> findAllUser(){
		return UserService.findAll();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Object> findById(@PathVariable Long id) {
		User User = UserService.findById(id);
		if(User == null) {
			return new ResponseEntity<Object>("User with ID " + id + " not found", HttpStatus.BAD_REQUEST);
		}
		else {
			return ResponseEntity.ok(User);
		}
	}
	
	@PostMapping
	public User createUser(@RequestBody User User) {
		User.setId(0L);
		return UserService.create(User);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Object> updateUser(@PathVariable Long id,@RequestBody User User) {
//		User User = UserService.findById(id);
		if(UserService.findById(id) == null) {
			return new ResponseEntity<Object>("User with ID " + id + " not found", HttpStatus.BAD_REQUEST);
		}
		else {
			User.setId(id);
			return ResponseEntity.ok(UserService.update(User));
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteUser(@PathVariable Long id){
		User User = UserService.findById(id);
		if(User == null) {
			return new ResponseEntity<Object>("User with ID " + id + " not found", HttpStatus.BAD_REQUEST);
		}
		else {
			UserService.delete(User);
			return ResponseEntity.ok("User has been deleted");
		}
	}
}
