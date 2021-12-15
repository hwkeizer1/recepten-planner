package nl.recipes.services;

import java.util.List;

import org.springframework.stereotype.Service;

import nl.recipes.domain.Tag;
import nl.recipes.exceptions.AlreadyExistsException;
import nl.recipes.repositories.TagRepository;

@Service
public class TagService {

	private final TagRepository tagRepository;

	public TagService(TagRepository tagRepository) {
		this.tagRepository = tagRepository;
	}
	
	public List<Tag> findAll() {
		return tagRepository.findAll();
	}
	
	public Tag create(Tag tag) throws AlreadyExistsException {
		if (tagRepository.findByName(tag.getName()).isPresent()) {	
			throw new AlreadyExistsException("Categorie " + tag.getName() + " bestaat al");
		}
		return tagRepository.save(tag);
	}
	
}
