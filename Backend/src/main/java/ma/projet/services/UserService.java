package ma.projet.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ma.projet.dao.IDao;
import ma.projet.entities.User;
import ma.projet.repository.UserRepository;

@Service
public class UserService  implements IDao<User>{
	@Autowired
	private UserRepository userRepository;

	@Override
	public User create(User o) {
		return userRepository.save(o);
	}

	@Override
	public boolean delete(User o) {
		try {
			userRepository.delete(o);
			return true;
		}
		catch(Exception ex) {
			return false;
		}
	}

	@Override
	public User update(User o) {
		return userRepository.save(o);
	}

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public User findById(Long id) {
		return userRepository.findById(id).orElse(null);
	}
	

}
