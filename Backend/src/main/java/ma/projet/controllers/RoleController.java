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
import ma.projet.entities.Role;
import ma.projet.repository.RoleRepository;
import ma.projet.services.RoleService;

@RestController
@RequestMapping("/api/v1/roles")
//@RequiredArgsConstructor
public class RoleController {
	
	@Autowired
	private RoleService RoleService;
	

	@GetMapping
	public List<Role> findAllRole(){
		return RoleService.findAll();
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Object> findById(@PathVariable Long id) {
		Role Role = RoleService.findById(id);
		if(Role == null) {
			return new ResponseEntity<Object>("Role with ID " + id + " not found", HttpStatus.BAD_REQUEST);
		}
		else {
			return ResponseEntity.ok(Role);
		}
	}
	
	@PostMapping
	public Role createRole(@RequestBody Role Role) {
		Role.setId(0L);
		return RoleService.create(Role);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Object> updateRole(@PathVariable Long id,@RequestBody Role Role) {
//		Role Role = RoleService.findById(id);
		if(RoleService.findById(id) == null) {
			return new ResponseEntity<Object>("Role with ID " + id + " not found", HttpStatus.BAD_REQUEST);
		}
		else {
			Role.setId(id);
			return ResponseEntity.ok(RoleService.update(Role));
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteRole(@PathVariable Long id){
		Role Role = RoleService.findById(id);
		if(Role == null) {
			return new ResponseEntity<Object>("Role with ID " + id + " not found", HttpStatus.BAD_REQUEST);
		}
		else {
			RoleService.delete(Role);
			return ResponseEntity.ok("Role has been deleted");
		}
	}
}
